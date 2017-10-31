package com.reeman.reemansdk.login;


import android.content.Intent;

import com.reeman.reemansdk.ReeManApplication;
import com.reeman.reemansdk.interfaces.ReemanLoginListener;
import com.reeman.reemansdk.service.IMService;
import com.reeman.reemansdk.utils.SPManager;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***
 * 登录成功返回的json解析类
 */
public class LoginParser {

    public static void parserLogin(String userName,String loginParser, ReemanLoginListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(loginParser);
            int ret = jsonObject.getInt("statusCode");
            if (ret != 0) {
                listener.onError("请求错误码："+ret);
            }
            if (ret == 0) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(0);
                String sig = jsonObjectSon.getString("sig");
                loginIm(userName,sig,listener);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
//                    String sig = jsonObjectSon.getString("sig");
//                    String admin = jsonObjectSon.getString("admin");
//                    String deviceid = jsonObjectSon.getString("deviceid");
//                    String userrole = jsonObjectSon.getString("userrole");
//                    String nickname = jsonObjectSon.getString("nickname");
//                    String usericon = jsonObjectSon.getString("usericon");
//                    String childSex = jsonObjectSon.getString("child_sex");
//                    String childAge = jsonObjectSon.getString("child_yearold");
//                    String childName = jsonObjectSon.getString("child_nickname");
//                }
            }

        } catch (JSONException e) {
            listener.onError("解析异常，请重试");
            e.printStackTrace();
        }
    }
    public static void loginIm(final String userName, final String userSig, final ReemanLoginListener loginListener) {
        ILiveLoginManager.getInstance().iLiveLogin(userName, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                loginListener.onSuccess(userSig);
                ReeManApplication.getContext().startService(new Intent(ReeManApplication.getContext(), IMService.class));
                SPManager.getEditor(ReeManApplication.getContext()).putString("userName", userName).commit();
                SPManager.getEditor(ReeManApplication.getContext()).putString("userSig", userSig).commit();
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                loginListener.onError(errMsg);
            }
        });
    }
}
