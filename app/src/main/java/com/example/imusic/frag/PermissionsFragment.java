package com.example.imusic.frag;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.imusic.R;
import com.example.imusic.util.ToastUtil;
import com.example.imusic.util.UiTool;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 权限获取
 */
public class PermissionsFragment extends BottomSheetDialogFragment
        implements EasyPermissions.PermissionCallbacks {
    // 权限回调的标识
    private static final int RC = 0x0100;

    public PermissionsFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_permissions, container, false);
        refreshState(root);
        root.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPerms();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 界面显示的时候进行刷新
        refreshState(getView());
    }

    /**
     * 刷新布局中的图片状态
     *
     * @param root 根布局
     */
    private void refreshState(View root) {
        if (root == null) {
            return;
        }
        Context context = getContext();
        root.findViewById(R.id.im_state_permission_read)
                .setVisibility(haveReadPerm(context) ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 是否有读取存储卡的权限
     *
     * @param context 上下文
     * @return 有则范围True
     */
    private static boolean haveReadPerm(Context context) {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 私有的show方法
     *
     * @param manager
     */
    private static void show(FragmentManager manager) {
        // 调用BottomSheetDialogFragment以及准备好的显示方法
        new PermissionsFragment()
                .show(manager, PermissionsFragment.class.getName());
    }

    /**
     * 检查是否有所有权限，便于以后扩展
     *
     * @param context 上下文
     * @param manager FragmentManager
     * @return 是返回True
     */
    public static boolean haveAll(Context context, FragmentManager manager) {
        boolean haveAll = haveReadPerm(context);
        if (!haveAll) {
            show(manager);
        }
        return haveAll;
    }

    /**
     * 申请权限的方法
     */
    @AfterPermissionGranted(RC)
    private void requestPerms() {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            ToastUtil.showToast(R.string.label_permission_ok);
            // Fragment 中调用getView()拿到根布局，前提是在onCreateView()之后
            refreshState(getView());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions)
                    , RC, perms);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        getDialog().dismiss();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // 如果权限有没有申请成功的权限存在，则弹出弹出框，用户点击后去到设置界面自己打开权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .build()
                    .show();
        }
    }

    /**
     * 权限申请的时候回调的方法，在这个方法把对应的权限申请状态交给EasyPermission框架
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 为了解决顶部状态栏黑色的问题
     */
    public static class TransStatusBottomSheetDialog extends BottomSheetDialog {

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if (window == null) {
                return;
            }
            // 得到屏幕的高度
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            //得到状态栏的高度
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());
            //计算dialog的高度
            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    dialogHeight<=0?ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);

        }
    }
}
