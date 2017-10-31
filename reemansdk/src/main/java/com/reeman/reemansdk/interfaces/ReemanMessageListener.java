package com.reeman.reemansdk.interfaces;

import com.tencent.TIMMessage;

/**
 * Created by melon on 2017/10/27.
 */

public interface ReemanMessageListener {
    void onReceiveMessage(TIMMessage timMessage);
}
