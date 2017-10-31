package com.reeman.reemansdk.utils.message;

import com.reeman.reemansdk.ReeManApplication;
import com.reeman.reemansdk.service.IMService;

import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_PICTURE;
import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_TEXT;
import static com.reeman.reemansdk.utils.message.ReemanMessage.MESSAGE_VIDEO;

/**
 * Created by melon on 2017/10/28.
 */

public class ReemanMessageSender {
    public static volatile ReemanMessageSender intance;
    private static TencentMessage tencentMessage;
    public static ReemanMessageSender getInstance(){
        if (intance == null) {
            synchronized (ReemanMessageSender.class) {
                if (intance == null) {
                    intance = new ReemanMessageSender();
                    if (tencentMessage == null) {
                        tencentMessage = new TencentMessage(ReeManApplication.getContext());
                    }
                }
            }
        }
        return intance;
    }

    /**
     * 文本消息
     * @param str
     * @param sendNumder
     */
    public void sendMessage(String str, String sendNumder) {
        sendMessage(MESSAGE_TEXT, str, sendNumder,null);
    }
    /**
     * 发消息
     * @param type 消息类型
     * @param path 图片，视频的路径
     * @param sendNumber 对方的手机号码
     */
    public void sendMessage(@IMService.ReemanMessageType int type, String path, String sendNumber, TencentMessage.MessageListener listener) {
        if (tencentMessage == null) {
            tencentMessage = new TencentMessage(ReeManApplication.getContext());
        }
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
        message.setMessagedesc(path);
        tencentMessage.sendMessage(message,sendNumber, listener);
    }
}
