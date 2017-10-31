package com.reeman.reemansdk.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by reeman on 2017/6/29.
 */

public class SingleToastUtils {
    private static Toast toast;

    public static void getSingleToast(Context context, String text) {
        if (toast == null) {
            //创建一个空的吐司/
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        //给吐司的内容设置自己想要的值
        toast.setText(text.toString());

        toast.show();//弹出吐司

    }public static void getSingleToastLong(Context context, String text) {
        if (toast == null) {
            //创建一个空的吐司/
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }
        //给吐司的内容设置自己想要的值
        toast.setText(text.toString());

        toast.show();//弹出吐司

    }

}
