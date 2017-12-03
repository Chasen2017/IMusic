package com.example.imusic.frag;



import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.imusic.AccountTrigger;
import com.example.imusic.R;


import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.edit_phone)
    EditText mPhoneEt;

    @BindView(R.id.edit_password)
    EditText mPasswordEt;

    @BindView(R.id.btn_submit)
    Button mSubmitBtn;

    @BindView(R.id.loading)
    Loading mLoading;

    private AccountTrigger mTrigger;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        // TODO 加载持久化数据保存的帐号密码信息

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 拿到Activity的引用
        mTrigger = (AccountTrigger) context;
    }

    // 跳转到注册页面
    @OnClick(R.id.txt_go_register)
    void showRegisterClick() {
        mTrigger.triggerView();
    }

    // 登录
    @OnClick(R.id.btn_submit)
    void login() {
        mLoading.start();
    }


}
