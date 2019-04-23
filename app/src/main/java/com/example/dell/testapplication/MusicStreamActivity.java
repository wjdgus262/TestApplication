package com.example.dell.testapplication;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MusicStreamActivity extends AppCompatActivity implements View.OnClickListener {

    Button rewind_btn,playpause_btn,forward_btn;
    BlastVisualizer mVisualizer;
    MediaPlayer mediaPlayer;
    String stream;
//    AudioService audioService = new AudioService();

    boolean prepared = false;
    boolean started = false;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_stream);


        rewind_btn = (Button)findViewById(R.id.strame_rewind);
        playpause_btn = (Button)findViewById(R.id.playbtn);
        forward_btn = (Button)findViewById(R.id.strame_foward);
        rewind_btn.setOnClickListener(this);
        playpause_btn.setOnClickListener(this);
        forward_btn.setOnClickListener(this);

        mVisualizer = (BlastVisualizer)findViewById(R.id.blast);
        int sessionId = AudioApplication.getInstance().getServiceInterface().getAudioSessionId();
//        Toast.makeText(getApplicationContext(),sessionId+"",Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if(sessionId != -1)
            mVisualizer.setAudioSessionId(sessionId);

//            mVisualizer.setAudioSessionId(audioSessionId);
//        Toast.makeText(getApplicationContext(),audioSessionId+"",Toast.LENGTH_SHORT).show();

        stream = getIntent().getExtras().getString("path");


        registerBroadcast();
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(prepared){
            mediaPlayer.release();
        }
        if(mVisualizer != null){
            mVisualizer.release();
        }
        unregisterBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            //mediaPlayer.start();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }
    private void updateUI() {
        if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
//            btn_play_pause.setImageResource(R.drawable.perpase);
            playpause_btn.setText("멈춤");
        } else {
//            btn_play_pause.setImageResource(R.drawable.play);
            playpause_btn.setText("시작");
        }
        Audio_item_1 audioItem = AudioApplication.getInstance().getServiceInterface().getAudioItem();
//        if (audioItem != null) {
//            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), audioItem.mAlbumId);
//            Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.empty_albumart).into(imageView);
//            textView_Title.setText(audioItem.mTitle);
//            webView = (WebView)findViewById(R.id.webview);
//            webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
//            webView.getSettings().setJavaScriptEnabled(true);
//            webView.loadUrl("https://www.youtube.com/results?search_query="+audioItem.mArtist);
//            webView.setWebChromeClient(new WebChromeClient());
//        } else {
//            imageView.setImageResource(R.drawable.empty_albumart);
//            textView_Title.setText("재생중인 음악이 없습니다.");
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.strame_rewind:
                AudioApplication.getInstance().getServiceInterface().rewind();
                int sessionId = AudioApplication.getInstance().getServiceInterface().getAudioSessionId();
//        Toast.makeText(getApplicationContext(),sessionId+"",Toast.LENGTH_SHORT).show();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(sessionId != -1)
                    mVisualizer.setAudioSessionId(sessionId);
                break;
            case R.id.playbtn:
                AudioApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.strame_foward:
                AudioApplication.getInstance().getServiceInterface().forward();;
                int id = AudioApplication.getInstance().getServiceInterface().getAudioSessionId();
//        Toast.makeText(getApplicationContext(),sessionId+"",Toast.LENGTH_SHORT).show();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(id != -1)
                    mVisualizer.setAudioSessionId(id);
                break;
        }
    }
}
