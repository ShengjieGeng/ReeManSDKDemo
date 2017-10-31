package com.reeman.reemansdk.interfaces;

import java.io.Serializable;

/**
 * Created by melon on 2017/10/25.
 */

public interface ReemanCallListener {
    public void onCallEnd(String msg, int endCode);

    public void onCallEstablished(int callId);

    public void onCalling(String msg);
}