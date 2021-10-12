package com.manuelsoft.mypomodoroapp.chronometer;

import android.content.Context;

import androidx.annotation.NonNull;

import com.manuelsoft.mypomodoroapp.R;
import com.manuelsoft.mypomodoroapp.audio.AudioPlayer;
import com.manuelsoft.mypomodoroapp.audio.VolumeContentObserver;

class SoundHelper {

    private final Context context;
    private AudioPlayer audioPlayer;
    private VolumeContentObserver volumeContentObserver;

    SoundHelper(@NonNull Context context) {
        assert context != null : "Context is null";
        this.context = context;
        setupAudio();
    }

    private void setupAudio() {
        audioPlayer = new AudioPlayer();
        audioPlayer.init();
        audioPlayer.loadSound(context, R.raw.clock);
        volumeContentObserver = new VolumeContentObserver(context, null,
                volume -> audioPlayer.setVolume(volume));
    }

    private void registerVolumeContentObserver() {
        context.getApplicationContext()
                .getContentResolver()
                .registerContentObserver(
                        android.provider.Settings.System.CONTENT_URI,
                        true,
                        volumeContentObserver);
    }

    private void unregisterVolumeContentObserver() {
        context.getApplicationContext()
                .getContentResolver()
                .unregisterContentObserver(volumeContentObserver);
    }

    public void play() {
        registerVolumeContentObserver();
        audioPlayer.play(volumeContentObserver.getCurrentVolume());
    }

    public void stop() {
        audioPlayer.stop();
        unregisterVolumeContentObserver();
    }

    public void release() {
        audioPlayer.release();
        unregisterVolumeContentObserver();
    }
}
