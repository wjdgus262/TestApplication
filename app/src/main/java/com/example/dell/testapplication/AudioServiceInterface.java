package com.example.dell.testapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class AudioServiceInterface {
    private ServiceConnection mServiceConnection;
    private AudioService mService;
    MainActivity mainActivity;
    public AudioServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
//        Toast.makeText(context,"A",Toast.LENGTH_SHORT).show();
    }

    public void setPlayList(ArrayList<Long> audioIds) {
        if (mService != null) {
            mService.setPlayList(audioIds);
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);
//               Log.i("제에발제에발","가");
//            mainActivity.test_function();
            ((MainActivity)MainActivity.mContext).updateUI();
            ((MainActivity)MainActivity.mContext).test_function();
        }

    }

    public void play() {
        if (mService != null) {
            mService.play();
//            Toast.makeText(g)

        }
    }

    public void pause() {
        if (mService != null) {
            mService.play();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
            ((MainActivity)MainActivity.mContext).updateUI();
            ((MainActivity)MainActivity.mContext).test_function();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
            ((MainActivity)MainActivity.mContext).test_function();
            ((MainActivity)MainActivity.mContext).updateUI();

        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.play();
        }
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public Audio_item_1 getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }

}
