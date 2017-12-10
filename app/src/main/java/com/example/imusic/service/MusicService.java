package com.example.imusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * 音乐播放服务
 */

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    public class MusicBinder extends Binder {

        //得到MediaPlayer状态的方法，仅不空且播放才是true
        public boolean callGetMPStatus(){
            return getMPStatus();
        }

        //呼叫播放/暂停方法
        public void callPlay(String path) {
            playMusic(path);
        }

        //呼叫停止方法
        public void callStop() {
            stopMusic();
        }

        //呼叫播放到指定进度方法
        public void callSeekTo(int mesc) {
            seekToMusic(mesc);
        }

        /**
         * 获取正在播放的歌曲进度
         */
        public int callGetCurrentPosition() {
            return getCurrentPosition();
        }
    }

    //得到MediaPlayer的状态：是否播放（空也是不播放）
    private boolean getMPStatus(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){//播放
            return true;
        }else {
            return false;
        }
    }

    //播放/暂停音乐
    private void playMusic(String path) {
        //打开软件播放第一首歌/换歌时mediaPlayer才是空，不空就是暂停/播放
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //MediaPlayer播放完一首歌的广播事件
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Intent intent=new Intent();
                    intent.setAction("com.example.imusic.ThisSongEnd");
                    sendBroadcast(intent);
                }
            });
            return;
        }
        //media不为空，就是要暂停/继续
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    //停止音乐，换歌时（上/下一首）才需要调用
    private void stopMusic() {
        if (mediaPlayer != null) {//只要是不为空，当前音乐不管播不播放都该释放掉
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //播放到指定进度
    private void seekToMusic(int mesc){
        if (mediaPlayer != null)
            mediaPlayer.seekTo(mesc);
    }

    //返回当前播放音乐的进度
    private int getCurrentPosition(){
        int t = 0;
        if (mediaPlayer != null) {
            t = mediaPlayer.getCurrentPosition();
        }
        return t;
    }

    @Override
    public void onCreate() {
        Log.i("TAG", "服务创建了 ");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("TAG", "服务被绑定了 ");
        return new MusicBinder();
    }

    @Override
    public void onDestroy() {
        stopMusic();
        super.onDestroy();
    }
}
