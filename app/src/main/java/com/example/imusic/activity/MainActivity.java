package com.example.imusic.activity;

import android.content.Context;
import android.content.Intent;
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
import com.example.imusic.util.MusicUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends ActivityCollector {

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.nav_view)
    NavigationView mNavView;

    @BindView(R.id.list_view)
    ListView musicListView;

    private MusicAdapter musicAdapter;

    private List<Song> musicList = new ArrayList<Song>();

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
    }

    //时间转换
    private String getTime(int duration) {
        duration /= 1000;
        int hour = duration/3600;       //时
        int minute = (duration-hour*3600)/60;   //分
        int seconds = duration-hour*3600-minute*60;  //秒

        if(hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, seconds);
        }
        return String.format("%02d:%02d", minute, seconds);
    }

    private void initWidget() {
        View headView = mNavView.inflateHeaderView(R.layout.header_main);
        TextView mNameTv = headView.findViewById(R.id.txt_user_name);
        TextView mCancellation = headView.findViewById(R.id.txt_cancellation);
        TextView mExit = headView.findViewById(R.id.txt_exit);
        mNameTv.setText(Account.name+"");
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

    //上一首
    public void before(View view) {
    }

    //播放/暂停
    public void star(View view) {
    }

    //下一首
    public void next(View view) {
    }
}
