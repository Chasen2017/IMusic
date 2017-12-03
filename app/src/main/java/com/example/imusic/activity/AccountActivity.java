package com.example.imusic.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.imusic.AccountTrigger;
import com.example.imusic.frag.LoginFragment;
import com.example.imusic.R;
import com.example.imusic.frag.RegisterFragment;

import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by asus-pc on 2017/12/2.
 */

public class AccountActivity extends AppCompatActivity implements AccountTrigger {

    // 用于Fragment记录和切换
    private Fragment mCurFragment;
    private Fragment mLoginFragment;
    private Fragment mRegisterFragment;

    @BindView(R.id.im_bg)
    ImageView mBg;

    // onCreate方法使用这个，用另外一个会使碎片页面加载不出来
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initWidget();
    }

    /**
     * 初始化控件
     */
    private void initWidget() {
        ButterKnife.bind(this);
        // 初始化Fragment
        mCurFragment = mLoginFragment = new LoginFragment();
        // 默认加载登录页面
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();

        // 初始化背景
        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()
                .into(new ViewTarget<ImageView, GlideDrawable>(mBg) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Drawable drawable = resource.getCurrent();
                        // 使用适配类进行包装
                        drawable = DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(), R.color.colorAccent),
                                PorterDuff.Mode.SCREEN); // 设置着色的效果和颜色，朦版模式
                        this.view.setImageDrawable(drawable);
                    }
                });

    }

    @Override
    public void triggerView() {
        Fragment fragment;
        // 当前为登录页面，切换为注册页面
        if (mCurFragment == mLoginFragment) {
            // 第一次点击时为空，创建一个，以后点击都不新建了
            if (mRegisterFragment == null) {
                mRegisterFragment = new RegisterFragment();
            }
            fragment = mRegisterFragment;
        } else {
            fragment = mLoginFragment;
        }
        // 判断后赋值给当前的Fragment
        mCurFragment = fragment;
        // 切换显示
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_container, fragment)
                .commit();
    }
}