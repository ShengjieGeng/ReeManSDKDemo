package com.reeman.reemansdk.customcall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.reeman.reemansdk.R;
import com.reeman.reemansdk.service.IMService;
import com.reeman.reemansdk.utils.PlayerUtil;
import com.tencent.callsdk.ILVCallManager;

import java.io.Serializable;

/**
 * Created by melon on 2017/10/24.
 */

public class CallInActivity extends Activity implements Serializable {

    private Animation right;
    private ImageView video_robot_tou;
    private Button btnAccept;
    private Button btnRefuse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callin);
        initView();
        initListener();
        initCall();
    }

    private void initCall() {
        Intent intent = getIntent();
        onNewIncomingCall(this, intent.getIntExtra("callId", 0), intent.getIntExtra("callType", 0), intent.getStringExtra("hostId"));
    }

    private void initBroadCast() {
        System.out.println("melon:初始化广播");
        IntentFilter filter = new IntentFilter();
        filter.addAction(IMService.NEW_INCOMING_CALL);
        registerReceiver(myReceiver,filter);
    }
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("melon:收到广播");
            if (action.equals(IMService.NEW_INCOMING_CALL)) {
                onNewIncomingCall(context, intent.getIntExtra("callId", 0), intent.getIntExtra("callType", 0), intent.getStringExtra("hostId"));
            }
        }
    };
    private int callId;
    private int callType;
    private String hostID;
    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }
    boolean isLocked = false;

    public void onNewIncomingCall(Context context, int callId, int callType, String getSponsorId) {
        System.out.println("melon---callId=="+callId);
        System.out.println("melon---callType=="+callType);
        System.out.println("melon---getSponsorId=="+getSponsorId);
        setCallId(callId);
        setCallType(callType);
        setHostID(getSponsorId);
        playRing();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        wl.setReferenceCounted(false);
        //判断是否锁屏，如果锁屏则唤醒
        if (!pm.isScreenOn()) {
            isLocked = true;
            wl.acquire();
        } else {
            isLocked = false;
        }
    }
    private void initView() {
        video_robot_tou = (ImageView) findViewById(R.id.video_robot_tou);
        btnAccept = (Button) findViewById(R.id.btn_video_accept);
        btnRefuse = (Button) findViewById(R.id.btn_video_refuse);
    }

    private void initListener() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptCall(getCallId(),getCallType(),getHostID());
                stopRing();
            }
        });
        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILVCallManager.getInstance().rejectCall(getCallId());
                stopRing();
                finish();
            }
        });
    }
    private void acceptCall(int callId, int callType, String hostID) {
        Intent intent = new Intent();
        intent.setClass(this, CallActivity.class);
        intent.putExtra("HostId", hostID);
        intent.putExtra("CallId", callId);
        intent.putExtra("CallType", callType);
        startActivity(intent);
        finish();
    }
    private void playRing() {
        right = AnimationUtils.loadAnimation(this, R.anim.yaobait);
        video_robot_tou.startAnimation(right);
        initPlayer();
    }
    private void stopRing() {
        IMService.receivedCall = false;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }
    PlayerUtil player;
    MediaPlayer mediaPlayer;
    private void initPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.incoming);
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
