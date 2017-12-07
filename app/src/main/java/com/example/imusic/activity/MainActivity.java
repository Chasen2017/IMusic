package com.example.imusic.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.imusic.R;
import com.example.imusic.adapter.MusicAdapter;
import com.example.imusic.bean.Song;
import com.example.imusic.presistence.Account;
import com.example.imusic.service.MusicService;
import com.example.imusic.util.MusicUtil;
import com.example.imusic.util.ToastUtil;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.imusic.util.MusicUtil.getTime;

public class MainActivity extends ActivityCollector {

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.nav_view)
    NavigationView mNavView;

    @BindView(R.id.list_view)
    ListView musicListView;

    private MusicAdapter musicAdapter;

    public static List<Song> musicList;

    public Song song;

    @BindView(R.id.tv_nowSong)
    TextView titleTV;

    @BindView(R.id.tv_nowSinger)
    TextView artistTV;

    @BindView(R.id.all_duration)
    TextView allDuration;

    @BindView(R.id.now_duration)
    TextView nowDuation;

    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    @BindView(R.id.play)
    ImageButton playBtn;

    private MyConn myConn;

    private MusicService.MusicBinder musicBinder;

    int nowMusicIndex;//当前播放的音乐的下标

    /**
     * MainActivity的入口方法
     * @param context 从context跳转到MainActivity
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Account.load();
        ActivityCollector.addActivity(this);
        initWidget();
        // 沉浸式状态栏
        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });

        allDuration.setText(getTime(0));
        nowDuation.setText(getTime(0));
        musicList = MusicUtil.getMusicData(this);
        musicAdapter = new MusicAdapter(this, musicList);
        musicListView.setAdapter(musicAdapter);

        myConn=new MyConn();
        bind();
        nowMusicIndex=0;

    }

    private void initWidget() {
        View headView = mNavView.inflateHeaderView(R.layout.header_main);
        TextView mNameTv = headView.findViewById(R.id.txt_user_name);
        TextView mCancellation = headView.findViewById(R.id.txt_cancellation);
        TextView mExit = headView.findViewById(R.id.txt_exit);
        TextView mRefresh = headView.findViewById(R.id.txt_refresh);
        mNameTv.setText(Account.name+"");
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 刷新音乐列表
                ToastUtil.showToast(R.string.refresh_success);
            }
        });
        mCancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 注销登录，跳转到登录页面
                Account.saveIsLogin(false);
                AccountActivity.show(MainActivity.this);
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出
                ActivityCollector.finishAll();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public void bind(){
        Intent intent=new Intent(this, MusicService.class);
        bindService(intent,myConn,BIND_AUTO_CREATE);
    }

    @OnClick(R.id.play)
    void playOnClicked() {
        song = musicList.get(nowMusicIndex);//当前播放的音乐
        musicBinder.callPlay(song.getPath());
    }

    @OnClick(R.id.before)
    public void before(View view) {
        musicBinder.callStop();
        nowMusicIndex--;
        if(nowMusicIndex<0){
            nowMusicIndex=musicList.size()-1;
        }
        song = musicList.get(nowMusicIndex);//当前播放的音乐
        musicBinder.callPlay(song.getPath());
    }

    @OnClick(R.id.next)
    public void next(View view) {
        musicBinder.callStop();
        nowMusicIndex++;
        if(nowMusicIndex>=musicList.size()){
            nowMusicIndex=0;
        }
        song = musicList.get(nowMusicIndex);//当前播放的音乐
        musicBinder.callPlay(song.getPath());
    }

    private class MyConn implements ServiceConnection {

        /**
         * 服务被绑定时候调用的方法
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            musicBinder= (MusicService.MusicBinder) iBinder;
        }

        /**
         * 当服务失去绑定时调用的方法，当服务异常终止的时候。
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

}
