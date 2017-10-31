package com.reeman.reemansdk;

import android.content.Context;

import com.tencent.TIMManager;
import com.tencent.ilivesdk.ILiveSDK;

/**
 * Created by melon on 2017/10/23.
 */

public class ReeManApplication {
    private volatile static ReeManApplication reeManApplication;

    public static Context getContext() {
        return context;
    }

    private static Context context;
    public static ReeManApplication getInstance(Context context) {
        reeManApplication.context = context;
        if (reeManApplication == null) {
            synchronized (ReeManApplication.class) {
                if (reeManApplication == null) {
                    reeManApplication = new ReeManApplication();
                }
            }
        }
        return reeManApplication;
    }

    /**
     * 初始化reemanSDK
     */
    public void initReemanApplication() {
        initCallSDK();
        //初始化imsdk
        TIMManager.getInstance().init(context);
    }
    /**
     * 初始化打电话功能
     */
    private void initCallSDK() {
        int DEMO_SDK_APP_ID = 1400011181;
        int DEMO_ACCOUNT_TYPE = 5812;
        ILiveSDK.getInstance().initSdk(context, DEMO_SDK_APP_ID, DEMO_ACCOUNT_TYPE);
    }
}
