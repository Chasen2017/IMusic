package com.example.imusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * 音乐播放服务
 */

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    public static MediaPlayer mediaPlayer;

    public class MusicBinder extends Binder{

        /**
         * 内部人员帮助我们调用服务内的方法
         */
        public void callPlay(String path){
            playMusic(path);
        }
        // 暂停更合适
        public void callPause(){
            pauseMusic();
        }

    }

    private void playMusic(String path){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseMusic(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
//            mediaPlayer.release();
        }
    }

    @Override
    public void onCreate() {
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onDestroy() {
        mediaPlayer=null;
        super.onDestroy();
    }
}
