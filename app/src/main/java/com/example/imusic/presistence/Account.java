package com.example.imusic.presistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.imusic.App;
import com.example.imusic.activity.AccountActivity;

/**
 * 账户类，与用户帐号密码有关数据的操作
 */

public class Account {

    public static String phone; // 帐号
    public static String password; // 密码
    public static String name; // 用户名
    public static boolean isLogin = false;

    /**
     * 持久化保存用户注册的信息
     * @param name 用户名
     * @param phone 帐号
     * @param password 密码
     */
    public static void save(String name, String phone, String password) {
        SharedPreferences sp = App.getContext().getSharedPreferences(AccountActivity.class.getName(), Context.MODE_PRIVATE);
        sp.edit()
                .putString("name", name)
                .putString("password", password)
                .putString("phone", phone)
                .apply();
    }

    /**
     * 当登录成功时，存储起来
     * @param isLogin
     */
    public static void saveIsLogin(boolean isLogin) {
        SharedPreferences sp = App.getContext().getSharedPreferences(AccountActivity.class.getName(), Context.MODE_PRIVATE);
        sp.edit().putBoolean("is_login", isLogin).apply();

    }
    /**
     * 加载本地保存的帐号、密码、用户名和是否登录过了
     */
    public static void load() {
        SharedPreferences sp = App.getContext().getSharedPreferences(AccountActivity.class.getName(), Context.MODE_PRIVATE);
        phone = sp.getString("phone", "");
        password = sp.getString("password", "");
        name = sp.getString("name", "");
        isLogin = sp.getBoolean("is_login", false);
    }

    /**
     * 判断输入的帐号密码是否与本地存储的帐号密码匹配
     * @param phone 帐号
     * @param password 密码
     * @return 是否匹配，是返回True
     */
    public static boolean check(String phone, String password) {
        if (phone.equals(Account.phone) && password.equals(Account.password)) {
            return true;
        }
        return false;
    }
}
