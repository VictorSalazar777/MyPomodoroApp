package com.manuelsoft.mypomodoroapp.audio;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;


public class VolumeContentObserver extends ContentObserver {
    private final AudioManager audioManager;
    private final OnChangeVolume onChangeVolume;
    public interface OnChangeVolume {
        void onChange(int volume);
    }
    public static final String TAG = VolumeContentObserver.class.getName();

    public VolumeContentObserver(Context context, Handler handler, OnChangeVolume onChangeVolume) {
        super(handler);
        this.onChangeVolume = onChangeVolume;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public int getCurrentVolume() {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "STREAM_MUSIC " + currentVolume);
        return currentVolume;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    @Override
    public void onChange(boolean selfChange) {
        int currentVolume = getCurrentVolume();
        onChangeVolume.onChange(currentVolume);
    }
}
