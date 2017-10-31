package com.reeman.reemansdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by melon on 2017/10/23.
 */

public class SPManager {
    private static volatile SharedPreferences sharedPreferences;
    private static volatile SharedPreferences.Editor editor;
    public static SharedPreferences.Editor getEditor(Context context) {
        if (editor == null) {
            synchronized (SPManager.class) {
                if (editor == null) {
                    sharedPreferences = getSharedPreferences(context);
                    editor = sharedPreferences.edit();
                }
            }
        }
        return editor;
    }
    public static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            synchronized (SPManager.class) {
                if (sharedPreferences == null) {
                    sharedPreferences = context.getSharedPreferences("reemandatas", Context.MODE_PRIVATE);
                }
            }
        }
        return sharedPreferences;
    }
}
