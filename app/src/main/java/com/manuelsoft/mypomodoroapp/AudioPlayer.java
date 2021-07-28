package com.manuelsoft.mypomodoroapp;


import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

public class AudioPlayer {
    private static final String TAG = AudioPlayer.class.getName();
    private SoundPool soundPool;
    private int soundClock;
    private int streamId;


    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
    }

    public void loadSound(Context context, int resourceId) {
        if (soundPool != null) {
            soundClock = soundPool.load(context, resourceId, 1);
        }
    }

    public void play(float initialVolume) {
        if (soundPool != null) {
            float soundpoolVolume = initialVolume / 15.0f;
            streamId = soundPool.play(soundClock, soundpoolVolume, soundpoolVolume, 0, -1, 1);
            Log.d(TAG, String.valueOf(soundpoolVolume));
        }
    }

    public void stop() {
        if (soundPool != null) {
            soundPool.stop(streamId);
        }
    }

    public void pause() {
        if (soundPool != null) {
            soundPool.pause(streamId);
        }
    }

    public void resume() {
        if (soundPool != null) {
            soundPool.resume(streamId);
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public void setVolume(int volume) {
        if (soundPool != null) {
            float soundpoolVolume = volume / 15.0f;
            soundPool.setVolume(streamId, soundpoolVolume, soundpoolVolume);
            Log.d(TAG, String.valueOf(soundpoolVolume));
        }
    }
}



