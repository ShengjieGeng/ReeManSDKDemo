package com.reeman.reemansdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by reeman on 2017/6/21.
 */

public class NetUtil {

    public static final String TAG = "NetUtil";
    public static final int PHONE_4G = 1;
    public static final int WIFI = 0;

    /***
     * 判断当前有么有网络
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param context Context
     * @return true 判断网络是wifi还是手机
     */
    public static int getNetWorkStyle(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                Log.i(TAG, "当前网络是wifi");
                return WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                Log.i(TAG, "当前网络是手机");
                return PHONE_4G;
            }
        }
        return -1;
    }

}
