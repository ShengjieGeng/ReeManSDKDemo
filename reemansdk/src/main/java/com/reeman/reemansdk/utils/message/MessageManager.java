package com.reeman.reemansdk.utils.message;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reeman.reemansdk.ReeManSDKManager;
import com.reeman.reemansdk.interfaces.ReemanMessageListener;
import com.reeman.reemansdk.service.IMService;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserProfile;
import java.util.Observable;

/**
 * Created by Administrator on 2017/4/26.
 */

public class MessageManager {
    private static MessageManager   instance;
    private Context mContext;
    private        TIMUserProfile   mSenderProfile;

    private MessageManager() {
    }

    public static MessageManager getInstance () {
        if (instance == null) {
            instance = new MessageManager();
            messageListener = ReeManSDKManager.getInstance().getMessageListener();
        }
        return instance;
    }
    static ReemanMessageListener messageListener;
    public static final String TAG           = "MessageManager";
    public static final String DELETE_UPDATE = "DELETE_UPDATE";
    IMService service = new IMService();
    public void update (Context context, Observable observable, Object data) {
        this.mContext = context;
        if (observable instanceof MessageEvent) {
            TIMMessage msg = (TIMMessage) data;
            if (messageListener != null) {
                messageListener.onReceiveMessage(msg);
            }
            if (msg == null)
                return;
            long    time   = msg.timestamp();
            TIMElem elem   = msg.getElement(0);
            String sender = msg.getSender();

            long currentTime = System.currentTimeMillis();
            long minute      = (currentTime - time * 1000);

            Log.i("sender----------------", sender);
            TIMElemType elemType = elem.getType();
            Log.d(TAG, "elem type: " + elemType.name());
            if (elemType == TIMElemType.Text) {
                TIMTextElem e    = (TIMTextElem) elem;
                String code = e.getText();
                disPathMessage(code, sender, msg, minute);
                TIMTextElem elem1 = (TIMTextElem) elem;
                String msgrcv = elem1.getText();
                System.out.println("=====" + msgrcv);
                if (msgrcv.contains("robotCallVolume")) {
                    System.out.println("收到音量信息==="+msgrcv);
                    String spStr[] = msgrcv.split(":");
                    if (spStr.length > 1 && spStr[1] != null) {
                        System.out.println("zhangsan  spStr[1]: " + spStr[1]);
                        int volume = Integer.parseInt(spStr[1]);
                        System.out.println("zhangsan  volume: " + volume);
                        Intent i = new Intent("com.reeman.showCallVolume");
                        i.putExtra("Volume", volume);
                        mContext.sendBroadcast(i);
                    }
                }else if (msgrcv.startsWith("reemancalling")) {
                    System.out.println("MessageManager;:::收到心跳");
                    context.sendBroadcast(new Intent(IMService.CALL_IS_RUNING));
                } else if (msgrcv.startsWith("remote_cancel")) {
                    callEnded = true;
                }
            }
        }
    }
//    ImageUtil mImageUtil;
    //为了防止通话界面卡死
    public static boolean callEnded=false;
    /**
     * 视屏结束
     *
     * @param code
     */
    private void disPathMessage (String code, String sender, TIMMessage msg, long time) {
        System.out.println("MessageManager:::::code--------" + code);
    }



}
