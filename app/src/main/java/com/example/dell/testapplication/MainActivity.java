package com.example.dell.testapplication;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editText;
    private final static int LOADER_ID = 0x001;
    private List<Audio_item_1> mArrayList;
    private RecyclerView recyclerview;
    private AudioAdapter_1 mAdapter;
    private String[] test_title = new String[1000];
    CircleImageView mImgAlbumArt;
    TextView mTxtTitle;

    CircleImageView album_Art;
    TextView album_mainText;
    TextView album_sub;
    ImageView btnrewind;
    ImageView btnplaypause;
    ImageView btnfoward;
    LinearLayout lin_min_player;

    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        //상태바 색상 변경
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setStatusBarColor(Color.parseColor("#ec6387"));
        }

        //권환체크
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);
            }else{
                getAudioListFromMediaDatabase(getApplicationContext());
            }
        }else{
            getAudioListFromMediaDatabase(getApplicationContext());
        }


        //EDITTEXT 검색
        editText = (EditText)findViewById(R.id.edit_text);
        editText.setCursorVisible(false);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER){
                    return true;
                }
                return false;
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)v).setCursorVisible(true);
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String text = v.getText().toString();
                v.setCursorVisible(true);
                return true;
            }
        });

        editText.addTextChangedListener(watcher);

        album_Art = (CircleImageView)findViewById(R.id.album_art);
        album_mainText = (TextView)findViewById(R.id.album_maintext);
        album_mainText.setSelected(true);
        album_sub = (TextView)findViewById(R.id.album_sub);
        btnplaypause = (ImageView)findViewById(R.id.btn_play_pause);
        lin_min_player = (LinearLayout)findViewById(R.id.lin_miniplayer);
        lin_min_player.setVisibility(View.INVISIBLE);
        findViewById(R.id.lin_miniplayer).setOnClickListener(this);
        findViewById(R.id.btn_rewind).setOnClickListener(this);
        btnplaypause.setOnClickListener(this);
        findViewById(R.id.btn_forward).setOnClickListener(this);
        registerBroadcast();
        updateUI();
//        btnrewind.setOnClickListener(this);
//        btnplaypause.setOnClickListener(this);
//        btnfoward.setOnClickListener(this);
//        lin_min_player.setOnClickListener(this);
//        Intent intent = new Intent(MainActivity.this,SileUpActivity.class);
//        startActivity(intent);
    }


    //EDITTEXT 검색
    TextWatcher watcher = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable s) {
            //텍스트 변경 후 발생할 이벤트를 작성.
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            //텍스트의 길이가 변경되었을 경우 발생할 이벤트를 작성.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            //텍스트가 변경될때마다 발생할 이벤트를 작성.
//            Toast.makeText(getApplicationContext(),s+"",Toast.LENGTH_SHORT).show();
            mAdapter.getFilter().filter(s);
        }

    };


    //권환체크
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getAudioListFromMediaDatabase(getApplicationContext());
        }
    }

    int index = 0;
    //현재 장치에 저장되어있는 음악 리스트 가져오기
    private void getAudioListFromMediaDatabase(final Context context){
        mArrayList = new ArrayList<Audio_item_1>();

        getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                };
                String section = MediaStore.Audio.Media.IS_MUSIC + " =1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getApplicationContext(),uri,projection,section,null,sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//                mArrayList = new ArrayList<>();
                if(cursor != null && cursor.getCount() > 0)
                {

                    while(cursor.moveToNext())
                    {
                        //arraylist에 저장
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                       String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                      String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        long dur = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long albumid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                       mArrayList.add(new Audio_item_1(id,title,artist,dur,albumid,index));
                        index++;
                    }

                    //리사이클뷰 생성,adapter 셋팅
                    recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerview.setLayoutManager(layoutManager);



                    mAdapter = new AudioAdapter_1(context,mArrayList);
                    recyclerview.setAdapter(mAdapter);

                }

//                mAdapter.swapCursor(cursor);
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
//                mAdapter.swapCursor(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_miniplayer:
                break;
            case R.id.btn_rewind:
                AudioApplication.getInstance().getServiceInterface().rewind();
//                updateUI();
                break;
            case R.id.btn_play_pause:
                AudioApplication.getInstance().getServiceInterface().togglePlay();
//                updateUI();
                break;
            case R.id.btn_forward:
                AudioApplication.getInstance().getServiceInterface().forward();
//                updateUI();
                break;
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
//            Toast.makeText(context,"지금",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    public void updateUI() {
//        Toast.makeText(getApplicationContext(),"지금",Toast.LENGTH_SHORT).show();
        if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
            btnplaypause.setImageResource(R.drawable.pause);
        } else {
            btnplaypause.setImageResource(R.drawable.play);
        }
        Audio_item_1 audioItem = AudioApplication.getInstance().getServiceInterface().getAudioItem();
        if (audioItem != null) {
            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),audioItem.AlbumId);
            Glide.with(getApplicationContext()).load(albumArtUri).error(R.drawable.empty_album_img).into(album_Art);
            album_mainText.setText(audioItem.title);
            album_sub.setText(audioItem.subTitle);
        } else {
            album_Art.setImageResource(R.drawable.empty_album_img);
            album_mainText.setText("재생중인 음악이 없습니다.");
        }
    }
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }
    public void test_function(){
//        Toast.makeText(getApplicationContext(),"?....되나?..",Toast.LENGTH_SHORT).show();
//        lin_miniplayer.setVisibility(View.VISIBLE);
        lin_min_player.setVisibility(View.VISIBLE);
        btnplaypause.setImageResource(R.drawable.pause);
    }


}
