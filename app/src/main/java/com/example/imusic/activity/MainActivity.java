package com.example.imusic.activity;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.util.Log;
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

    private MusicService.MusicBinder musicBinder;

    int nowMusicIndex;//当前播放的音乐的下标

    public static final int UPDATE = 1;

    MusicReceiver musicReceiver;

    //处理seekBar进度条的显示
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE:
                    //若只考虑有歌情况，这个判断就没什么用了
                    if (musicBinder != null && song.getDuration() != 0) {
                        nowDuration.setText(getTime(musicBinder.callGetCurrentPosition()));
                        seekBar.setProgress(100*musicBinder.callGetCurrentPosition()/song.getDuration());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private ServiceConnection myConn =new ServiceConnection(){

        /**
         * 服务被绑定时候调用的方法
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("TAG", "得到中间人 ");
            musicBinder = (MusicService.MusicBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    /**
     * MainActivity的入口方法
     *
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
        musicReceiver = new MusicReceiver();
        registerReceiver(musicReceiver,new IntentFilter("com.example.imusic.ThisSongEnd"));
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
        mNameTv.setText(Account.name + "");
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicBinder.callStop();//先停掉音乐
                musicList = MusicUtil.getMusicData(MainActivity.this);
                //跟初始化的逻辑一样
                if(musicList.size()>0){
                    nowMusicIndex=0;
                    song = musicList.get(nowMusicIndex);//当前播放的音乐，刷新列表后初始化是第一首歌
                    updateView();//要判mediaplayer空了……
                    ToastUtil.showToast(R.string.refresh_success);
                }else {
                    //没歌就结束退出啦
                    //扫描导致了没歌，服务已经被创建绑定了，要解绑
                    ToastUtil.showToast(R.string.none_Song);
                    removeService();
                    ActivityCollector.finishAll();
                    return;
                }
            }
        });
        mCancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 注销登录，跳转到登录页面
                Account.saveIsLogin(false);
                musicBinder.callStop();
                AccountActivity.show(MainActivity.this);
            }
        });
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 正常退出，先解绑，后结束
                removeService();
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
                musicBinder.callStop();//先停掉当前音乐，然后才是播放
                nowMusicIndex=position;
                song = musicList.get(nowMusicIndex);
                playOnClicked();//就相当于设了当前音乐后点击播放按钮嘛
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if(musicList.size()>0){//有歌初始化
            nowMusicIndex = 0;
            song = musicList.get(nowMusicIndex);//当前播放的音乐，初始化是第一首歌
            titleTV.setText(song.getSongName());
            artistTV.setText(song.getSinger());
            allDuration.setText(MusicUtil.getTime(song.getDuration()));
        }else {
            //没歌就结束退出啦
            //还没创建绑定服务呢，所以不用解绑
            ToastUtil.showToast(R.string.none_Song);
            ActivityCollector.finishAll();
            return;
        }
        bind();
        //初始化时song已经指向了第一首歌
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int dest = seekBar.getProgress();
                    int mMax = song.getDuration();
                    int sMax = seekBar.getMax();
                    musicBinder.callSeekTo(mMax*dest/sMax);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //监听播放进度
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message message = new Message();
                    message.what = UPDATE;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //混合开启服务，先开启，后绑定，拿到中间人即可解绑,退出前停止服务
    public void bind() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, myConn, Context.BIND_AUTO_CREATE);
    }

    // 先解绑，后停止服务
    public void removeService(){
        unbindService(myConn);
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
        unregisterReceiver(musicReceiver);
    }

    //更新各部件信息
    public void updateView() {
        // 切换播放按钮样式
        if (musicBinder.callGetMPStatus()) {
            playBtn.setBackgroundResource(R.drawable.ic_pause);//音乐在播放，按钮显示暂停
        } else {
            playBtn.setBackgroundResource(R.drawable.ic_play);//音乐没在播放，按钮显示播放
        }
        titleTV.setText(song.getSongName());
        artistTV.setText(song.getSinger());
        allDuration.setText(MusicUtil.getTime(song.getDuration()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myConn);
        ActivityCollector.removeActivity(this);
    }

    //播放/暂停按钮的点击事件
    @OnClick(R.id.ib_play)
    void playOnClicked() {
        //先执行了播放/暂停再变更按钮，虽然打破原则，但是不用判空（执行播放效果，之前肯定是空，判了结果肯定是false，没用）
        musicBinder.callPlay(song.getPath());
        //更新显示信息
        updateView();
    }

    //上一首
    @OnClick(R.id.ib_before)
    public void before() {
        if (musicList.size() == 0) return;//没有歌曲就不用向下执行了（没歌进不来，这句代码没用了）
        musicBinder.callStop();//先停掉当前音乐，然后才是播放
        nowMusicIndex--;
        if (nowMusicIndex < 0) {
            nowMusicIndex = musicList.size() - 1;
        }
        song = musicList.get(nowMusicIndex);//当前播放的音乐
        musicBinder.callPlay(song.getPath());
        //更新显示信息
        updateView();
    }

    //下一首
    @OnClick(R.id.ib_next)
    public void next() {
        if (musicList.size() == 0) return;//没有歌曲就不用向下执行了（没歌进不来，这句代码没用了）
        musicBinder.callStop();//先停掉当前音乐，然后才是播放
        nowMusicIndex++;
        if (nowMusicIndex >= musicList.size()) {
            nowMusicIndex = 0;
        }
        song = musicList.get(nowMusicIndex);//当前播放的音乐
        musicBinder.callPlay(song.getPath());
        //更新显示信息
        updateView();
    }

    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TAG", "接收到一首歌曲播放完成的消息，自动播放下一首");
            next();
        }
    }

}
