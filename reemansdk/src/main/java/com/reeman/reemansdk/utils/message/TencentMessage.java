package com.reeman.reemansdk.utils.message;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.reeman.reemansdk.utils.Constans;
import com.reeman.reemansdk.utils.SingleToastUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFileElem;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

public class TencentMessage {

    public static final int MAX_TEXT_MSG_LENGTH = 1 * 1024;
    public TIMConversation conversation;
    Context context;
    String TAG = "AVProgress";

    public TencentMessage(Context context) {
        super();
        this.context = context;
    }

    /***
     * 消息发送通用方法
     *
     * @param message
     *            封装的消息(参数:消息类型,消息描述(文字就文字,图片,视频发送路径))
     * @param peerid
     * @param listener
     */
    public void sendMessage(ReemanMessage message, String peerid, MessageListener listener) {
        int type = message.getMessagetype();
        String desc = message.getMessagedesc();
        switch (type) {
            case ReemanMessage.MESSAGE_TEXT:
                sendRobotCode(desc, peerid, listener);
                break;
            case ReemanMessage.MESSAGE_VIDEO:
                sendVideo(desc, peerid,  listener);
                break;
            case ReemanMessage.MESSAGE_PICTURE:
                sendPicture(desc, peerid, listener);
                break;
            case ReemanMessage.MESSAGE_SOUND:
                listener.onSendingError("暂未开通");
                break;
            case ReemanMessage.MESSAGE_FILE:
                listener.onSendingError("暂未开通");
                break;
            default:
                break;
        }
    }

    ;

    /**
     * 手机同时发送指令多台设备机器人
     *
     * @param str      信息
     * @param sendName 接受者
     */
    public void sendCodeMore(String str, String sendName, final MessageListener listener) {
//
//        Log.i("main", "====发送成功" + str + "//" + sendName);
//        if (str.length() == 0) {
//            return;
//        }
//        conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, sendName);
//        try {
//            byte[] byte_num = str.getBytes("utf8");
//            if (byte_num.length > MAX_TEXT_MSG_LENGTH) {
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        TIMMessage msg = new TIMMessage();
//        TIMTextElem elem = new TIMTextElem();
//        elem.setText(str);
//        int iRet = msg.addElement(elem);
//        if (iRet != 0) {
//            Log.d(TAG, "add element error:" + iRet);
//            return;
//        }
//        Log.d(TAG, "ready send text msg");
//        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {// 发送消息回调
//            public void onError(int code, String desc) {
////                if (code == Constans.TEXT_MSG_FAILED_FOR_TOO_LOGNG) {
////                    desc = "消息太长";
////                } else if (code == Constans.SEND_MSG_FAILED_FOR_PEER_NOT_LOGIN) {
////                    desc = "对方账号不存在或未登陆过！";
////                }
////                Log.e(TAG, "send message failed. code: " + code + " errmsg: " + desc);
////                final int err_code = code;
////                if (err_code == 6013) {
////                    listener.loginError();
////                } else if (err_code == 6012) {
////                    MyToastView.getInstance().Toast(context, "网络不可用，请重连！");
////                }
//            }
//
//            public void onSuccess(TIMMessage arg0) {
//            }
//        });
    }

    /**
     * 指令发送
     *
     * @param str      内容
     * @param peerid   设备ID
     * @param listener 发送监听
     */
    public void sendRobotCode(String str, String peerid, final MessageListener listener) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(peerid)) {
            if (listener != null)
                listener.onSendingError("号码为空");
            return;
        }
        if (TextUtils.isEmpty(peerid)) {
            if (listener!=null)
                listener.onSendingError("号码为空");
            return;
        }
        conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerid);
        try {
            byte[] byte_num = str.getBytes("utf8");
            if (byte_num.length > MAX_TEXT_MSG_LENGTH) {
                if (listener!=null)
                listener.onSendingError("消息过长");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        TIMMessage msg = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(str);
        int iRet = msg.addElement(elem);
        if (iRet != 0) {
            if (listener!=null)
            listener.onSendingError("add element error:" + iRet);
            return;
        }
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {// 发送消息回调
            public void onError(int code, String desc) {// 发送消息失败
                if (code == Constans.TEXT_MSG_FAILED_FOR_TOO_LOGNG) {
                    desc = "消息太长";
                    if (listener!=null)
                    listener.onSendingError(desc);
                } else if (code == Constans.SEND_MSG_FAILED_FOR_PEER_NOT_LOGIN) {
                    desc = "对方账号不存在或未登陆过！";
                    if (listener!=null)
                    listener.onSendingError(desc);
                }else if (code == 6013) {
                    if (listener!=null)
                    listener.onSendingError("登录异常");
                } else if (code == 6012) {
                    if (listener!=null)
                        listener.onSendingError("网络不可用，请重连！");
                } else {
                    listener.onSendingError("send message failed. code: " + code + " errmsg: " + desc);
                }
            }

            public void onSuccess(TIMMessage arg0) {
                if (listener!=null)
                listener.onSendingSuccess();
            }
        });
    }

    // level 0: 原图发送 1: 高压缩率图发送(图片较小，默认值) 2:高清图发送(图片较大)

    public void sendPicture(String path,String peerId, final MessageListener listener) {
        if (path.length() == 0) {
            if (listener!=null)
                listener.onSendingError("路径为空");
            return;
        }
        String peerIdentifier = peerId;
        if (TextUtils.isEmpty(peerIdentifier)) {
            if (listener!=null)
            listener.onSendingError("收信人号码为空");
            return;
        }
        conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerIdentifier);
        TIMMessage msg = new TIMMessage();
        try {

            TIMImageElem elem = new TIMImageElem();
            elem.setLevel(0);
            elem.setPath(path);
            if (0 != msg.addElement(elem)) {
                if (listener!=null)
                listener.onSendingError("add image element error");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {
            // 发送消息回调
            public void onError(int code, String desc) {
                if (code == Constans.SEND_MSG_FAILED_FOR_PEER_NOT_LOGIN) {
                    if (listener!=null)
                    listener.onSendingError("对方账号不存在或未登陆过！");
                } else if (code == 6013) {
                    if (listener!=null)
                        listener.onSendingError("登录异常");
                } else {
                    if (code == 6012) {
                        if (listener!=null)
                        listener.onSendingError("网络不可用，请重连！");
                    } else {
                        if (listener!=null)
                        listener.onSendingError("分享失败");
                    }
                }
            }

            public void onSuccess(TIMMessage msg) {
                if (listener!=null)
                listener.onSendingSuccess();
            }
        });

    }

    public void sendVideo(String path, String peerid,  final MessageListener listener) {
        if (path.length() == 0) {
            if (listener != null) {
                listener.onSendingError("路径为空");
            }
            return;
        }
        String peerIdentifier = peerid;
        if (TextUtils.isEmpty(peerIdentifier)) {
            if (listener != null)
            listener.onSendingError("发送号码为空");
            return;
        }
        //构造一条消息
        TIMMessage msg = new TIMMessage();
//添加文件内容
        TIMFileElem elem = new TIMFileElem();
        elem.setPath(path); //设置文件路径
        elem.setFileName("family_" + System.currentTimeMillis()); //设置消息展示用的文件名称
        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            if (listener != null)
            listener.onSendingError("addElement failed");
            return;
        }
        //发送消息
        if (conversation == null) {
            conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerIdentifier);
        }
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                if (listener != null)
                listener.onSendingError("send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                if (listener != null)
                listener.onSendingSuccess();
            }
        });
    }

    /**
     * 用于监听发送消息异常的监听
     */
    public interface MessageListener {

        void onSendingSuccess();

        void onSendingError(String msg);
    }

}
