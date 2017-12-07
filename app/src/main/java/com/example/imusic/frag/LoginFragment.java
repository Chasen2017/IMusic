package com.example.imusic.frag;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.imusic.AccountTrigger;

import com.example.imusic.R;
import com.example.imusic.activity.MainActivity;
import com.example.imusic.presistence.Account;
import com.example.imusic.util.ToastUtil;

import net.qiujuer.genius.ui.widget.Loading;


import butterknife.BindView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录的Fragment
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

    //帐号密码
    private String phone;
    private String password;


    private AccountTrigger mTrigger;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        // 加载保存的数据
        mPhoneEt.setText(Account.phone+"");
        mPasswordEt.setText(Account.password+"");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // 判断是否登录过了，如果登录过了，则直接跳转到MainActivity
        if (Account.isLogin) {
            MainActivity.show(getContext());
        }
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
        String pho = mPhoneEt.getText().toString().trim();
        String pw = mPasswordEt.getText().toString().trim();
        // 帐号密码不为空，且匹配，跳转到MainActivity
        if (TextUtils.isEmpty(pho) || TextUtils.isEmpty(pw)) {
            ToastUtil.showToast(R.string.login_fail);
            mLoading.stop();
            return;
        }
        if (Account.check(pho, pw)) {
            // 匹配成功，保存登录状态，跳转到MainActivity
            Account.saveIsLogin(true);
            mLoading.stop();
            getActivity().finish();
            MainActivity.show(getContext());
        } else {
            ToastUtil.showToast(R.string.login_fail);
            mLoading.stop();
        }
    }

}
