package com.example.imusic.frag;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.imusic.AccountTrigger;
import com.example.imusic.R;
import com.example.imusic.activity.AccountActivity;
import com.example.imusic.util.ToastUtil;

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


    public RegisterFragment() {
    }

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
        // 检查输入情况
        if (!checkInput()) {
            return;
        }
        mLoading.start();
        // 持久化保存
        SharedPreferences sp = getContext().getSharedPreferences(AccountActivity.class.getName(), Context.MODE_PRIVATE);
        sp.edit()
                .putString("name", mNameEt.getText().toString().trim())
                .putString("password", mPasswordEt.getText().toString().trim())
                .putString("phone", mPhoneEt.getText().toString().trim())
                .apply();
        mLoading.stop();
        //注册成功之后跳转到登录页面
        ToastUtil.showToast(R.string.register_success);
        mTrigger.triggerView();
    }

    /**
     * 检查注册所需三项内容是否正确
     * @return
     */
    private boolean checkInput() {
        String phone = mPhoneEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();
        String name = mNameEt.getText().toString().trim();
        // 电话为空
        if(TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(R.string.error_phone_empty_input);
            return false;
        }
        // 电话号码不为11位
        if (phone.length() != 11) {
            ToastUtil.showToast(R.string.error_phone_input);
            return false;
        }
        // 密码为空
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToast(R.string.error_password_input);
            return false;
        }
        // 名字为空
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToast(R.string.error_name_input);
            return false;
        }
        return true;
    }

}
