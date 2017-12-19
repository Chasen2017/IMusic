package com.example.imusic.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity管理类
 */

public class ActivityCollector extends AppCompatActivity {

    public static List<Activity> activities = new ArrayList<>();

    // 添加activity
    public static void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    // 移除activity
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    // 全部移除
    public static void finishAll() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }
}
