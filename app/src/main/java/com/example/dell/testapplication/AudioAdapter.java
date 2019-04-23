package com.example.dell.testapplication;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AudioAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> implements Filterable {
    ArrayList<Long> audioIds;
    public static ArrayList<AudioItem> alist;
    ArrayList<AudioItem> audiolist;
    Context context;
    public AudioAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        AudioItem audioItem = AudioItem.bindCursor(cursor);
        ((AudioViewHolder)viewHolder).setAudioItem(audioItem,cursor.getPosition());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem,parent,false);
       return new AudioViewHolder(v);
    }

    public static class AudioItem{
        public long mId;
        public long mAlbumId;
        public String mTitle;
        public String mArtist;
        public String mAlbum;
        public long mDuration;
        public String mDataPath;

        public static AudioItem bindCursor(Cursor cursor){
            AudioItem audioItem = new AudioItem();
            Log.i("TestTitle",cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))+"");
            audioItem.mId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
            audioItem.mAlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            audioItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            audioItem.mArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            audioItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            audioItem.mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            return audioItem;
        }
    }
    public ArrayList<Long> getAudiIds(){
        int count = getItemCount();
        audioIds = new ArrayList<>();
        for(int i = 0; i < count; i++){
            audioIds.add(getItemId(i));
        }
        return audioIds;
    }


    private class AudioViewHolder extends RecyclerView.ViewHolder{
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private CircleImageView mImgAlbumArt;
        private TextView mTxtTitle;
        private TextView mTxtSubTitle;
        private TextView mTxtDuration;
        private AudioItem mItem;
        private int mPosition;
        public AudioViewHolder(final View itemView) {
            super(itemView);
            mImgAlbumArt = (CircleImageView)itemView.findViewById(R.id.albumimg);
            mTxtTitle = (TextView)itemView.findViewById(R.id.albumtitle);
            mTxtSubTitle = (TextView)itemView.findViewById(R.id.albumartist);
            mTxtDuration = (TextView)itemView.findViewById(R.id.albumduration);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void setAudioItem(AudioItem item, int position){
            Log.i("Test audioItem",item.mTitle);
            mItem = item;
            mPosition = position;
            mTxtTitle.setText(item.mTitle);
            mTxtSubTitle.setText(item.mArtist +"("+item.mAlbum+")");
            mTxtDuration.setText(android.text.format.DateFormat.format("mm:ss",item.mDuration));
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri,item.mAlbumId);
//            Glide.with(itemView.getContext()).load(albumArtUri).error((int)R.drawable.empty_album_img).into(mImgAlbumArt);
            Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.empty_album_img).into(mImgAlbumArt);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<AudioItem> list = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                list.addAll(audiolist);
            }else{
                String filterPattren = charSequence.toString().toLowerCase().trim();
                for(AudioItem item : audiolist){
                    if(item.mTitle.toLowerCase().contains(filterPattren)){
                        list.add(item);
                    }
                }
            }
            FilterResults result = new FilterResults();
            result.values = list;
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            alist.clear();
            alist.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };
}
