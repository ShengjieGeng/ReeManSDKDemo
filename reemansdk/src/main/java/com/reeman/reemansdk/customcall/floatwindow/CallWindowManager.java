package com.reeman.reemansdk.customcall.floatwindow;

import android.content.Context;
import android.view.WindowManager;

import java.util.Timer;

/**
 * Created by melon on 2017/9/8.
 * 悬浮窗管理
 * 创建，移除
 * 单例模式
 */

public class CallWindowManager {

    private CallFloatView normalView;

    private static CallWindowManager instance;

    private CallWindowManager() {
    }

    public static CallWindowManager getInstance() {
        if (instance == null)
            instance = new CallWindowManager();
        return instance;
    }

    /**
     * 创建小型悬浮窗
     * @param time 通话时间
     */
    public void createNormalView(Context context, int time) {
        if (normalView == null)
            normalView = new CallFloatView(context,time);
    }

    public Timer getTimeConsumeTimer() {
        return normalView.getTimer();
    }

    public int getTime() {
        return normalView.getTime();
    }
    /**
     * 移除悬浮窗
     *
     * @param context
     */
    public void removeNormalView(Context context) {
        if (normalView != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(normalView);
            normalView = null;
        }
    }
}

