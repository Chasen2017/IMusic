package com.example.imusic.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;

/**
 *  Ui工具类
 */


public class UiTool {
    private static int STATUS_BAR_HEIGHT = -1;

    /**
     * 得到状态栏的高度
     * @param activity Activity
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && STATUS_BAR_HEIGHT == -1) {
            try {
                // 尝试获取status_bar_height这个属性的Id对应的资源int值
                final Resources res = activity.getResources();
                int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId <= 0) {
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen")
    ;
                    Object object = clazz.newInstance();
                    resourceId = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
                }
                // 如果拿到了就直接调用获取值
                if (resourceId > 0) {
                    STATUS_BAR_HEIGHT = activity.getResources().getDimensionPixelOffset(resourceId);
                }
                //如果还是未拿到
                if (STATUS_BAR_HEIGHT <= 0) {
                    //通过Window拿取
                    Rect rectangle = new Rect();
                    Window window = activity.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    STATUS_BAR_HEIGHT = rectangle.top;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }


}
