package com.reeman.reemansdk.Registe;

import com.reeman.reemansdk.interfaces.ReemanRegisteListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 注册返回的json解析
 */

public class RegisterParser {

    public static void parserRegister(String loginParser, ReemanRegisteListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(loginParser);
            int ret = jsonObject.getInt("statusCode");
            if (ret != 0) {
                if (ret == 1) {
                    listener.onError("错误请求：账号已存在");
                } else if (ret == 2) {
                    listener.onError("错误请求：数据库连接失败");
                }else if (ret == 3) {
                    listener.onError("错误请求：获取签名失败");
                } else {
                    listener.onError("错误请求：" +ret);
                }
            } else if (ret == 0) {
                listener.onSuccess();
            }
        } catch (JSONException e) {
            listener.onError("解析异常，请重试");
            e.printStackTrace();
        }
    }

}
