package com.reeman.reemansdk.interfaces;

/**
 * Created by melon on 2017/10/23.
 */

public interface ReemanLoginListener {
    void onError(String msg);
    void onSuccess(String sig);
}
