package com.reeman.reemansdk.Registe;

import com.reeman.reemansdk.interfaces.ReemanRegisteListener;
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
 * Created by reeman on 2017/6/23.
 */

public class RegisterUtil {

    public static final int REQUEST_FAILED = -3;
    OkHttpClient mOkHttpClient;
    public static final String TAG = "login";
    private volatile static RegisterUtil instance;
    public static RegisterUtil getInstance() {
        if (instance == null) {
            synchronized (RegisterUtil.class) {
                if (instance == null) {
                    instance = new RegisterUtil();
                }
            }
        }
        return instance;
    }
    public void registeroWeb(String username, String password,final ReemanRegisteListener listener) {

        password = MD5.MD5(password);
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
        try {
            String url = "http://gzq.test.szgps.net:88/tlsuser.php";
            RequestBody requestBodyPost = new FormBody.Builder()
                    .add("action", "usRegist")
                    .add("username", username)
                    .add("password", password).build();
            Request requestPost = new Request.Builder().url(url)
                    .post(requestBodyPost).build();
            mOkHttpClient.newCall(requestPost).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onError("注册请求失败");
                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {
                    String htmlStr = response.body().string();
                    RegisterParser.parserRegister(htmlStr, listener);
                }
            });
        } catch (Exception e) {
            listener.onError("网络请求异常");
        }
    }

}
