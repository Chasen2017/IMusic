package com.example.imusic.frag;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.imusic.AccountTrigger;
import com.example.imusic.R;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册的Fragment
 */
public class RegisterFragment extends Fragment {

    @BindView(R.id.edit_phone)
    EditText mPhoneEt;

    @BindView(R.id.edit_password)
    EditText mPasswordEt;

    @BindView(R.id.edit_name)
    EditText mNameEt;

    @BindView(R.id.btn_submit)
    Button mSubmitBtn;

    @BindView(R.id.loading)
    Loading mLoading;

    private AccountTrigger mTrigger;


    public RegisterFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 拿到Activity的引用
        mTrigger = (AccountTrigger) context;
    }

    // 跳转到登录页面
    @OnClick(R.id.txt_go_login)
    void showLoginClick() {
        mTrigger.triggerView();
    }

    // 注册
    @OnClick(R.id.btn_submit)
    void register() {
        mLoading.start();
        // TODO 注册成功之后跳转到登录页面

    }





}
