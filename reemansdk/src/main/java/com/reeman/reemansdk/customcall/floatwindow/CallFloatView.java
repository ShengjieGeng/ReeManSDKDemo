package com.reeman.reemansdk.customcall.floatwindow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reeman.reemansdk.R;
import com.reeman.reemansdk.customcall.CallActivity;
import com.reeman.timeconsumelib.TimeUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by melon on 2017/9/8.
 */


public class CallFloatView extends LinearLayout {

    private Context context = null;
    private View view = null;
    private WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    private static WindowManager windowManager;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private boolean initViewPlace = false;
    private CallWindowManager myWindowManager;
    private boolean isControlViewShowing = false;
    private int time;
    private final TextView tv_time;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                System.out.println("float:修改时间："+time);
                tv_time.setText(TimeUtil.timeFormat(time));
            }
        }
    };
    /**
     *
     * @param context
     * @param time 通话时间
     */
    public CallFloatView(Context context, int time) {
        super(context);
        this.time = time;
        this.context = context;
        myWindowManager = CallWindowManager.getInstance();
        LayoutInflater.from(context).inflate(R.layout.call_float_window, this);
        view =findViewById(R.id.call_float_view);
        tv_time = (TextView) findViewById(R.id.tv_time);
        System.out.println("floatView:启动");
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initLayoutParams();
        initEvent();
        initTimeConsume();
    }

    private void initTimeConsume() {
        timeConsume=new Timer();
        timeConsume.schedule(new TimerTask() {
            @Override
            public void run() {
                time++;
                System.out.println("float:time===="+time);
                handler.sendEmptyMessage(1);
            }
        },0,1000 );
    }

    /**
     * 初始化参数
     */
    private void initLayoutParams() {
        //屏幕宽高
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        String packname = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
        if(permission){
            lp.type = WindowManager.LayoutParams.TYPE_PHONE;
        }else{
            lp.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按,不设置这个flag的话，home页的划屏会有问题
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        //悬浮窗默认显示的位置
        lp.gravity = Gravity.START | Gravity.TOP;
        //指定位置
        lp.x = screenWidth/2;
        lp.y = screenHeight/2;
        //悬浮窗的宽高
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.format = PixelFormat.TRANSPARENT;
        windowManager.addView(this, lp);
    }

    /**
     * 设置悬浮窗监听事件
     */
    float preRawX=0;
    float preRawY=0;
    float curRawX=0;
    float curRawY=0;
    private void initEvent() {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("float:ontouch=======Gelin");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!initViewPlace) {
                            initViewPlace = true;
                            //获取初始位置
                            mTouchStartX += (event.getRawX() - lp.x);
                            mTouchStartY += (event.getRawY() - lp.y);
                        } else {
                            //根据上次手指离开的位置与此次点击的位置进行初始位置微调
                            mTouchStartX += (event.getRawX() - x);
                            mTouchStartY += (event.getRawY() - y);
                        }
                        preRawX=event.getRawX();
                        preRawY=event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取相对屏幕的坐标，以屏幕左上角为原点
                        x = event.getRawX();
                        y = event.getRawY();
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        curRawX = event.getRawX();
                        curRawY = event.getRawY();
                        if (preRawY == curRawY & preRawX == curRawX) {
                            //TODO 点击小窗口跳转到通话页面
                            Intent intent = new Intent(context, CallActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            System.out.println("float:启动callActivity");
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 更新浮动窗口位置
     */
    private void updateViewPosition() {
        lp.x = (int) (x - mTouchStartX);
        lp.y = (int) (y - mTouchStartY);
        windowManager.updateViewLayout(this, lp);
    }
    Timer timeConsume;
    public Timer getTimer() {
        return timeConsume;
    }

    public int getTime() {
        return time;
    }
}
