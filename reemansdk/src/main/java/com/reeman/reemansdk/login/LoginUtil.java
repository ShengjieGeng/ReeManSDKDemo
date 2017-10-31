package com.reeman.reemansdk.login;

import com.reeman.reemansdk.interfaces.ReemanLoginListener;
import com.reeman.reemansdk.utils.MD5;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by reeman on 2017/6/22.
 */

public class LoginUtil {
    private static volatile LoginUtil instance;
    private OkHttpClient mOkHttpClient;

    public static LoginUtil getInstance() {
        if (instance == null) {
            synchronized (LoginUtil.class) {
                if (instance == null) {
                    instance =new LoginUtil();
                }
            }
        }
        return instance;
    }
    public synchronized void loginToWeb(final String username, String password, final ReemanLoginListener listener) {

        password = MD5.MD5(password);
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build();
        }
        try {
            //http://gzq.test.szgps.net:88/tlsuser.php
            String url = "http://gzq.test.szgps.net:88/tlsuser.php";
            RequestBody requestBodyPost = new FormBody.Builder()
                    .add("action", "usLogin")
                    .add("username", username)
                    .add("password", password).build();
            Request requestPost = new Request.Builder().url(url)
                    .post(requestBodyPost).build();
            mOkHttpClient.newCall(requestPost).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onError("登录请求失败");
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {
                    String htmlStr = response.body().string();
                    LoginParser.parserLogin(username,htmlStr, listener);
                }
            });
        } catch (Exception e) {
            listener.onError("网络请求异常");
        }
    }
}
