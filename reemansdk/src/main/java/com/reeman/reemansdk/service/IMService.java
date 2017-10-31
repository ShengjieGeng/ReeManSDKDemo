package com.reeman.reemansdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.reeman.reemansdk.ReeManSDKManager;
import com.reeman.reemansdk.customcall.CallInActivity;
import com.reeman.reemansdk.interfaces.ReemanUserStatusListener;
import com.reeman.reemansdk.utils.SPManager;
import com.reeman.reemansdk.utils.SingleToastUtils;
import com.reeman.reemansdk.utils.message.MessageEvent;
import com.reeman.reemansdk.utils.message.MessageManager;
import com.reeman.reemansdk.utils.message.ReemanMessage;
import com.reeman.reemansdk.utils.message.PicMessage;
import com.reeman.reemansdk.utils.message.TencentMessage;
import com.reeman.reemansdk.utils.message.TextMessage;
import com.reeman.reemansdk.utils.message.VideooMessage;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserStatusListener;
import com.tencent.TIMValueCallBack;
import com.tencent.callsdk.ILVCallConfig;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallNotification;
import com.tencent.callsdk.ILVCallNotificationListener;
import com.tencent.callsdk.ILVIncomingListener;
import com.tencent.callsdk.ILVIncomingNotification;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Observable;
import java.util.Observer;

import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_FILE;
import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_PICTURE;
import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_SOUND;
import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_TEXT;
import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_VIDEO;

public class IMService extends Service implements Observer, ILVCallNotificationListener {
    public static final String CALL_IS_RUNING = "com.reeman.iscalling";
    public static volatile IMService instancel;
    public static final String TAG = "IMService";
    public final static String BIND_OK = "com.jc.ok_bind";
    public final static String BIND_NO = "com.jc.no_bind";
    public final static String RECV_MSG = "com.jc.msg";
    public final static String SENDVIDEO_FAILURE = "send.video.success";
    TencentMessage tencentMessage;
    public final static String NEW_INCOMING_CALL="com.reeman.newincoming";
    public final static String MESSAGE_UNREAD = "com.reeman.message.unread";
    private ILVIncomingListener incomingListener;
    Handler handler = new Handler();
    public static IMService getInstance() {
        if (instancel == null) {
            synchronized (IMService.class) {
                if (instancel == null) {
                    instancel = new IMService();
                }
            }
        }
        return instancel;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instancel = this;
        SetMessageListener();
//        executeNotification();
    }

    private static final int CLOSE_REQUEST_CODE = 102;
    private static final int NOTIFICATION_FLAG = 103;
//    private void executeNotification() {
//
//        Notification.Builder mNoti = new Notification.Builder(this);
//        // 进入主页
////        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent mainIntent = PendingIntent.getActivity(getApplicationContext(), 111, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        mNoti.setContentIntent(mainIntent);
//
//        mNoti.setSmallIcon(R.mipmap.robot_icon);
//        Notification myNotify = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            myNotify = mNoti.build();
//        }
//        myNotify.flags = Notification.FLAG_NO_CLEAR;// 不能够自动清除
//        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.notificaton_service_toggle);
//        rv.setTextViewText(R.id.text_content, "Reeman");
//        rv.setImageViewResource(R.id.image, R.mipmap.robot_icon);
//
//        // 关闭服务
//        Intent closeIntent = new Intent(Constants.NOTIFITION_CLOSE_IMSERVICE);
//        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, CLOSE_REQUEST_CODE,
//                closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        rv.setOnClickPendingIntent(R.id.close, closePendingIntent);
//        myNotify.contentView = rv;
//        // 通过调用startForground方法使进程为前台进程
//        startForeground(NOTIFICATION_FLAG, myNotify);
//    }

    public static boolean receivedCall = false;
    private void initCallSDK() {
        System.out.println("MELON>>>>>>>>>imservice initCallSDK");
        ILVCallManager.getInstance().init(new ILVCallConfig()
                .setNotificationListener(this)
                .setMemberStatusFix(false)
                .setTimeOut(20)
                .setAutoBusy(true));
        if (incomingListener == null) {
            incomingListener = new ILVIncomingListener() {
                @Override
                public void onNewIncomingCall(int callId, int callType, ILVIncomingNotification notification) {
                    System.out.println("MELON::::::::来电话了");
                    if (!receivedCall) {
                        Intent intentMain = new Intent(getApplicationContext(), CallInActivity.class);
                        intentMain.putExtra("hostId", notification.getSponsorId());
                        SPManager.getEditor(getApplicationContext()).putString("deviceID", notification.getSponsorId()).commit();
                        intentMain.putExtra("callId", callId);
                        intentMain.putExtra("callType", callType);
                        intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentMain);
//                        Intent incomingCall = new Intent(NEW_INCOMING_CALL);
//                        incomingCall.putExtra("hostId", notification.getSponsorId());
//                        incomingCall.putExtra("callId", callId);
//                        incomingCall.putExtra("callType", callType);
//                        sendBroadcast(incomingCall);
                        receivedCall = true;
                    }
                }
            };
        } else {
            ILVCallManager.getInstance().removeIncomingListener(incomingListener);
        }
        ILVCallManager.getInstance().addIncomingListener(incomingListener);
    }

    public static boolean loginSuccesss = false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("IMService : onStartCommand");
        initCallSDK();
        isForce = false;
        loginIm(SPManager.getSharedPreferences(getApplicationContext()).getString("userName",""));
        SetForceLogout();
        userStatusListener = ReeManSDKManager.getInstance().getUserStatusListener();
        return super.onStartCommand(intent, flags, startId);
    }
    public void loginIm(String userName) {
        String userSig = SPManager.getSharedPreferences(getApplicationContext()).getString("userSig", "");
        if (!TextUtils.isEmpty(userSig)) {
            ILiveLoginManager.getInstance().iLiveLogin(userName, userSig, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if (!loginSuccesss) {
                        System.out.println("登录成功");
                        loginSuccesss = true;
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    System.out.println("登录失败");
                    loginSuccesss = false;
                }
            });
        }
    }

    public void logout() {
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                System.out.println("melon>>>>>>>exit:成功");
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                System.out.println("melon>>>>>>>exit:失败/errmsg:::"+errMsg);
            }
        });
    }

    public void SetMessageListener() {
        MessageEvent.getInstance().addObserver(this);
    }

    public static boolean isForce = false;
    ReemanUserStatusListener userStatusListener;
    private void SetForceLogout() {
        System.out.println("set force logout");
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            public void onForceOffline() {
                SingleToastUtils.getSingleToast(getApplicationContext(),"异地登录");
                if (!isForce) {
                    if (userStatusListener != null) {
                        userStatusListener.onForceLogout("异地登录，强制下线");
                    }
                    IMService.this.stopSelf();
                    isForce = true;
                }
            }
            @Override
            public void onUserSigExpired() {

            }
        });
    }
    /**
     * 文本消息
     * @param str
     * @param sendNumder
     */
    public void sendMessage(String str, String sendNumder) {
        sendMessage(MESSAGE_TEXT, str, sendNumder,null);
    }
    public void sendOnlyCode(String str, String num) {
        if (str.length() == 0) {
            return;
        }
        if (num.length() == 0) {
            return;
        }
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, num);
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(str);
        int iRet = msg.addElement(elem);
        if (iRet != 0) {
            return;
        }
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {// 发送消息回调
            public void onError(int code, String desc) {// 发送消息失败
                Log.i("main", "====机器人在线，发给手机失败");
            }

            public void onSuccess(TIMMessage arg0) {
                Log.i("main", "====机器人在线，发给手机成功过");
            }
        });
    }
    @IntDef({MESSAGE_TEXT, MESSAGE_VIDEO, MESSAGE_SOUND, MESSAGE_FILE, MESSAGE_PICTURE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReemanMessageType {
    }
    /**
     * 发消息
     * @param type 消息类型 如果是纯文本消息，info是string类型的字符串；如果是图片或者视频，info为路径
     * @param info 图片，视频的路径
     * @param sendNumber 对方的手机号码
     */
    public void sendMessage(@ReemanMessageType int type, String info, String sendNumber, TencentMessage.MessageListener listener) {
        if (tencentMessage == null) {
            tencentMessage = new TencentMessage(IMService.this);
        }
        System.out.println("info:"+info);
        ReemanMessage message = null;
        switch (type) {
            case MESSAGE_TEXT:
                message = new TextMessage();
                break;
            case MESSAGE_PICTURE:
                message = new PicMessage();
                break;
            case MESSAGE_VIDEO:
                message = new VideooMessage();
                break;
            default:
                break;
        }
        message.setMessagedesc(info);
        tencentMessage.sendMessage(message,sendNumber, listener);
    }

    public static String LBSAds = "还没有获取到定位信息呢";
    private Observable observable;
    private Object data;
    @Override
    public void update(Observable observable, Object data) {
        this.observable = observable;
        this.data = data;
        MessageManager.getInstance().update(IMService.this,observable,data);
    }

    @Override
    public void onDestroy() {
        System.out.println("IMService onDestroy");
        TIMManager.getInstance().logout();
        MessageEvent.getInstance().deleteObserver(this);
        ILVCallManager.getInstance().removeIncomingListener(incomingListener);
        if (!isForce)
        logout();
//        stopService(new Intent(getApplicationContext(), AccountService.class));
        super.onDestroy();
    }
    @Override
    public void onRecvNotification(int callid, ILVCallNotification notification) {
        System.out.println("IMSERVICE ::::callID:"+callid+";noti:"+notification.toString());
    }
}
