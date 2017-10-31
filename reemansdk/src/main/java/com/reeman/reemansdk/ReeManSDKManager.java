package com.reeman.reemansdk;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.reeman.reemansdk.Registe.RegisterUtil;
import com.reeman.reemansdk.customcall.CallActivity;
import com.reeman.reemansdk.interfaces.ReemanLoginListener;
import com.reeman.reemansdk.interfaces.ReemanMessageListener;
import com.reeman.reemansdk.interfaces.ReemanRegisteListener;
import com.reeman.reemansdk.interfaces.ReemanCallListener;
import com.reeman.reemansdk.interfaces.ReemanUserStatusListener;
import com.reeman.reemansdk.login.LoginUtil;
import com.reeman.reemansdk.service.IMService;
import com.reeman.reemansdk.utils.message.ReemanMessageSender;
import com.tencent.TIMManager;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by melon on 2017/10/23.
 */

public class ReeManSDKManager implements Serializable {
    private volatile static ReeManSDKManager instance;
    public static ReeManSDKManager getInstance() {
        if (instance == null) {
            synchronized (ReeManSDKManager.class) {
                if (instance == null) {
                    instance = new ReeManSDKManager();
                }
            }
        }
        return instance;
    }

    /**
     *
     * @param context
     * @param callNumber 拨打的电话号码
     * @param type 通话类型：TYPE_VIDEO,TYPE_MONITOR
     */
    public void call(Context context,String callNumber,@ReemanCallType int type,String callTip) {
        Intent videoIntent = new Intent();
        videoIntent.setClass(context, CallActivity.class);
        videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        videoIntent.putExtra("HostId", ILiveLoginManager.getInstance().getMyUserId());
        videoIntent.putExtra("CallId", 0);
        videoIntent.putExtra("type", type);
        videoIntent.putExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
        videoIntent.putExtra("callTip", callTip);
        ArrayList<String> nums = new ArrayList<>();
        nums.add(callNumber);
        videoIntent.putStringArrayListExtra("CallNumbers", nums);
        context.startActivity(videoIntent);
    }
    /**
     * @param userNumber 账号
     * @param pass 密码
     * @param listener 登录reeman回调
     */
    public void loginReeman(@NonNull String userNumber, @NonNull String pass, final ReemanLoginListener listener) {
        LoginUtil.getInstance().loginToWeb(userNumber,pass,listener);
    }
    /**
     * 注册reeman
     * @param number 账号,建议使用电话号码作为账号
     * @param pass 密码
     */
    public void registerReeman(@NonNull String number, @NonNull String pass, ReemanRegisteListener registeListener) {
        if (!isValidUserName(number)) {
            registeListener.onError("用户名为 4 ~ 24 个字符，不能为纯数字");
        } else if (!isValidPassword(pass)) {
            registeListener.onError("密码长度为 8 ~ 16 个字符");
        } else {
            RegisterUtil.getInstance().registeroWeb(number,pass,registeListener);
        }
    }
    public static final int TYPE_VIDEO=0;
    public static final int TYPE_MONITOR=1;

    public ReemanMessageSender getMessgeSender() {
        return ReemanMessageSender.getInstance();
    }

    @IntDef({TYPE_VIDEO, TYPE_MONITOR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReemanCallType {}

    public void onDestroy() {
        ReeManApplication.getContext().stopService(new Intent(ReeManApplication.getContext(), IMService.class));
    }
    private boolean isValidUserName(String userName) {
        return userName.length() >= 4 &&
                userName.length() <= 24 &&
                userName.matches("^[A-Za-z0-9]*[A-Za-z][A-Za-z0-9]*$");
    }
    private boolean isValidPassword(String password) {
        return password.length() >= 8
                && password.length() <= 16
                && password.matches("^[A-Za-z0-9]*$");
    }




    private ReemanCallListener listener;
    private ReemanUserStatusListener userStatusListener;
    private ReemanMessageListener reemanMessageListener;

    public ReemanUserStatusListener getUserStatusListener() {
        return userStatusListener;
    }public ReemanMessageListener getMessageListener() {
        return reemanMessageListener;
    }
    public ReemanCallListener getListener() {
        return listener;
    }
    public void addCallListener(ReemanCallListener listener) {
        this.listener = listener;
    }
    public void addStatusListener(ReemanUserStatusListener userStatusListener) {
        this.userStatusListener = userStatusListener;
    }public void addMessageListener(ReemanMessageListener reemanMessageListener) {
        this.reemanMessageListener = reemanMessageListener;
    }
}
