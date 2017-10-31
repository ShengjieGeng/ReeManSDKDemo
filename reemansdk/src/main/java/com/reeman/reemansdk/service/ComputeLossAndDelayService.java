package com.reeman.reemansdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.reeman.reemansdk.utils.http.HttpUtil;

/**
 * Created by reeman on 2017/7/27.
 */

public class ComputeLossAndDelayService extends Service {
    OnLossAndDelayListener lossAndDelayListener;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        lossAndDelayListener = new OnLossAndDelayListener();
        return lossAndDelayListener;
    }

    public String start(String pingCMD) {
        return HttpUtil.getLossAndDelay(pingCMD);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class OnLossAndDelayListener extends Binder {
        public ComputeLossAndDelayService getService(){
            return ComputeLossAndDelayService.this;
        };
    }

}
