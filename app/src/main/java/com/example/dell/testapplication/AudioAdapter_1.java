package com.example.dell.testapplication;

import android.content.ContentUris;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AudioAdapter_1 extends RecyclerView.Adapter<AudioAdapter_1.CustomViewHolder> implements Filterable {

    //필터리스트
    private List<Audio_item_1> arrayList;

    //원본 리스트
    private List<Audio_item_1> arrayListFull;


    private ArrayList<Audio_item_1> audioIds;

//    Audio_item_1 audio_item_1 = new Audio_item_1();
    //context
    MainActivity mainActivity = new MainActivity();
    private Context mcontext;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ImageView imageView;
    //viewholder create
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem,viewGroup,false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

//    public ArrayList<Audio_item_1> getAudioIds(){
//        int count = getItemCount();
//        for(int i = 0; i < count; i++){
//            audioIds.add(audio_item_1.getId());
//        }
//    }
    public ArrayList<Long> getAudioIds() {
    int count = getItemCount();
//    Log.i("COUNT",count+"");
    ArrayList<Long> audioIds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
        audioIds.add(arrayList.get(i).getId());

    }
    return audioIds;
}
    //bindviewholder // 객체생성, 클릭 이벤트
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, final int i) {
        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(artworkUri,arrayList.get(i).getAlbumId());
            customViewHolder.id.setText(Long.toString(arrayList.get(i).getId()));
            customViewHolder.index_1.setText(Integer.toString(arrayList.get(i).getIndex()));
            customViewHolder.title.setText(arrayList.get(i).getTitle());
            customViewHolder.title.setSelected(true);
            customViewHolder.subtitle.setText(arrayList.get(i).getSubTitle());
            customViewHolder.dur.setText(android.text.format.DateFormat.format("mm:ss",arrayList.get(i).getDuration()));
            Glide.with(mcontext).load(albumArtUri).error(R.drawable.empty_album_img).into(customViewHolder.circleImageView);
//            customViewHolder.circleImageView.setImageResource(R.drawable.background);

        customViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                TextView textView = (TextView)v.findViewById(R.id.albumtitle);
                TextView id = (TextView)v.findViewById(R.id.albumid);
                TextView index = (TextView)v.findViewById(R.id.albumindex);
                ImageView imageView = (ImageView)v.findViewById(R.id.btn_play_pause);
                int position = Integer.parseInt(index.getText().toString());
                String id_text = id.getText().toString();
//                Toast.makeText(context,i+"",Toast.LENGTH_SHORT).show();
//                imageView.setImageResource(R.drawable.pause);
                String text = textView.getText().toString();
//                mainActivity.btnplaypause = (ImageView)v.findViewById(R.id.btn_play_pause);


                AudioApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
                AudioApplication.getInstance().getServiceInterface().play(i);



//                Toast.makeText(context, id_text+"", Toast.LENGTH_LONG).show();
//                try {
//                    Uri musicURI = Uri.withAppendedPath(
//                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+id_text);
//                    mediaPlayer.reset();
//                    mediaPlayer.setDataSource(mcontext, musicURI);
////                mediaPlayer.prepare();
//                    mediaPlayer.start();
//                }catch (Exception e){
//
//                }


            }
        });
    }


    //생성자
    public AudioAdapter_1(Context context,List<Audio_item_1> list){
        this.arrayList = list;
        this.mcontext = context;
        arrayListFull = new ArrayList<>(list);
    }

    //count
    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }


    //filter(search)
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Audio_item_1> filterdList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filterdList.addAll(arrayListFull);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Audio_item_1 item_1 : arrayListFull){
                    if(item_1.getTitle().toLowerCase().contains(filterPattern)){
                        filterdList.add(item_1);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterdList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        protected  TextView id;
        protected TextView title;
        protected TextView subtitle;
        protected TextView dur;
        protected  TextView index_1;
        protected CircleImageView circleImageView;
        protected ImageView btnpaueplay;

        public CustomViewHolder(View view) {
            super(view);
            mView =view;
            this.id = (TextView)view.findViewById(R.id.albumid);
            this.title = (TextView) view.findViewById(R.id.albumtitle);
            this.subtitle = (TextView) view.findViewById(R.id.albumartist);
            this.dur = (TextView)view.findViewById(R.id.albumduration);
            this.circleImageView = (CircleImageView) view.findViewById(R.id.albumimg);
            this.index_1 = (TextView)view.findViewById(R.id.albumindex);
        }
    }
}
