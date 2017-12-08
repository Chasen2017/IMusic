package com.example.imusic.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    TextView nowDuration;

    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    @BindView(R.id.ib_play)
    ImageButton playBtn;

    private MyConn myConn;

    private MusicService.MusicBinder musicBinder;

    int nowMusicIndex;//当前播放的音乐的下标

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
        initData();
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
    }

    /**
     * 初始化控件
     */
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
        allDuration.setText(getTime(0));
        nowDuration.setText(getTime(0));
        musicList = MusicUtil.getMusicData(this);
        musicAdapter = new MusicAdapter(this, musicList);
        musicListView.setAdapter(musicAdapter);
        // ListView的item点击事件
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 点击事件的实现

            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        myConn=new MyConn();
        bind();
        nowMusicIndex=0;
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

    @OnClick(R.id.ib_play)
    void playOnClicked() {
        play();
    }

    /**
     * 调用播放的方法与切换图片，给item点击事件复用
     *
     * TODO 这里播放是有问题的，当点击暂停的时候，然后再点击播放按钮，是重复，而不是继续上面的播放，需要完善
     */
    private void play() {
        if (MusicService.mediaPlayer == null) {
            return;
        }
        // 切换播放按钮样式
        if (MusicService.mediaPlayer.isPlaying()) {
            musicBinder.callPause();
            playBtn.setBackgroundResource(R.drawable.ic_play);
        } else {
            song = musicList.get(nowMusicIndex);//当前播放的音乐
            musicBinder.callPlay(song.getPath());
            playBtn.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    @OnClick(R.id.ib_before)
    public void before(View view) {
        musicBinder.callPause();
        nowMusicIndex--;
        if(nowMusicIndex<0){
            nowMusicIndex=musicList.size()-1;
        }
        song = musicList.get(nowMusicIndex);//当前播放的音乐
        musicBinder.callPlay(song.getPath());
        playBtn.setBackgroundResource(R.drawable.ic_pause);
    }

    @OnClick(R.id.ib_next)
    public void next(View view) {
        musicBinder.callPause();
        nowMusicIndex++;
        if(nowMusicIndex>=musicList.size()){
            nowMusicIndex=0;
        }
        song = musicList.get(nowMusicIndex);//当前播放的音乐
        musicBinder.callPlay(song.getPath());
        playBtn.setBackgroundResource(R.drawable.ic_pause);
    }


}
