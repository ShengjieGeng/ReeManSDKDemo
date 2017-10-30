package com.reeman.reemansdkdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.reeman.reemansdk.ReeManApplication;


/**
 * Created by melon on 2017/10/24.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ReeManApplication.getInstance(getApplicationContext()).initReemanApplication();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
