package com.reeman.reemansdk.customcall.floatwindow;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by melon on 2017/9/9.
 */

public class CallFloatWindowService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CallWindowManager.getInstance().createNormalView(getApplicationContext(),intent.getIntExtra("time",0));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (CallWindowManager.getInstance().getTimeConsumeTimer()!=null) {
            CallWindowManager.getInstance().getTimeConsumeTimer().cancel();
            System.out.println("float:cancelTimer");
        }
        CallWindowManager.getInstance().removeNormalView(getApplicationContext());
    }
}
