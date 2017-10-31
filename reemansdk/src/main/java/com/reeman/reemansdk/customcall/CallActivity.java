package com.reeman.reemansdk.customcall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.reeman.reemansdk.R;
import com.reeman.reemansdk.ReeManSDKManager;
import com.reeman.reemansdk.customcall.floatwindow.CallWindowManager;
import com.reeman.reemansdk.interfaces.ReemanCallListener;
import com.reeman.reemansdk.service.ComputeLossAndDelayService;
import com.reeman.reemansdk.service.IMService;
import com.reeman.reemansdk.utils.AnimUtils;
import com.reeman.reemansdk.utils.message.MessageManager;
import com.reeman.reemansdk.utils.NetUtil;
import com.reeman.reemansdk.utils.PlayerUtil;
import com.reeman.reemansdk.utils.SingleToastUtils;
import com.tencent.av.opengl.ui.GLView;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.callsdk.ILVBCallMemberListener;
import com.tencent.callsdk.ILVCallConstants;
import com.tencent.callsdk.ILVCallListener;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.callsdk.ILVCallOption;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.ilivesdk.view.BaseVideoView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 通话界面
 */
public class CallActivity extends Activity implements GLView.OnTouchListener,ILVCallListener,SeekBar.OnSeekBarChangeListener,View.OnTouchListener, ILVBCallMemberListener, View.OnClickListener {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 101;
    private Button btnEndCall;
    private CheckBox btnCamera, btnMic, btnSpeaker;
    private AVRootView avRootView;
    private TextView tvTitle, tvLog;
    private RelativeLayout rlControl;
    private LinearLayout llBeauty;
    private SeekBar sbBeauty;

    private String mHostId;
    private int mCallId;
    private int mCallType;
    private int mBeautyRate;

    private boolean bCameraEnable = true;
    private boolean bMicEnalbe = true;
    private boolean bSpeaker = true;
    private int mCurCameraId = ILiveConstants.FRONT_CAMERA;

    private AVVideoCtrl avVideoCtrl = ILiveSDK.getInstance().getAvVideoCtrl();
    private ILVCallOption option;
    private View choicesView;
    private int videoType;
    private View head_control_layout;
    private ImageButton head_down;
    private ImageButton head_left;
    private ImageButton head_up;
    private ImageButton head_right;
    private CheckBox robot_control;
    public ComputeLossAndDelayService.OnLossAndDelayListener listener;
    private Intent lossAndDelay;
    private SeekBar seekBar;
    private TextView tvSignal;
    private ImageView ivSignal;
    private View ll_volume;
    private View callMainView;
    private View requestCallView;
    private Button btnEnd;
    private Animation shakeHeadAnim;
    private AVVideoView mainPicture;
    private AVVideoView subPicture;
    private int width;
    private int height;
    private String callNumber = "";
    private int choicesViewWidth;
    private int headControlWidth;
    private ImageView btn_beauty;
    private ImageView btn_charge;
    private Intent floatService;
    private ReemanCallListener reemanListener;
    private String callTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        win.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            win.setAllowEnterTransitionOverlap(true);
            win.setAllowReturnTransitionOverlap(true);
            Explode explode = new Explode();
            explode.setDuration(1000);
            win.setEnterTransition(explode);
            win.setExitTransition(explode);
        }
        setContentView(R.layout.activity_call);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        // 添加通话回调
        initListener();
        initCall();
        //计算丢包率和延迟服务
        getLossAndDelayThread();
        intiReceiver();
        initCallHeart();
    }
    /**
     * 输出日志
     */
    private void addLogMessage(String strMsg){
        String msg = tvLog.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        msg = msg + "\r\n["+formatter.format(curDate)+"] " + strMsg;
        System.out.println("logMessageg:::"+msg);
//        tvLog.setText(msg);
    }
    private void initView() {
        btnEnd = (Button) findViewById(R.id.video_main_weijitong_guaduan);
        btnEnd.setOnClickListener(this);
        requestCallView = findViewById(R.id.requestCallView);
        callMainView = findViewById(R.id.callMainView);
        video_robot_tou = (ImageView) findViewById(R.id.video_robot_tou);
        btn_beauty = (ImageView) findViewById(R.id.btn_beauty);
        btn_charge = (ImageView) findViewById(R.id.btn_charge);
        ll_volume = findViewById(R.id.ll_volume);
        llBeauty = (LinearLayout) findViewById(R.id.ll_beauty_setting);
        choicesView = findViewById(R.id.choices);
        head_control_layout = findViewById(R.id.head_control_layout);
        calculateWidth();
        head_down = (ImageButton) findViewById(R.id.head_down);
        head_left = (ImageButton) findViewById(R.id.head_left);
        head_up = (ImageButton) findViewById(R.id.head_up);
        head_right = (ImageButton) findViewById(R.id.head_right);
        robot_control = (CheckBox) findViewById(R.id.robot_control);
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        btnEndCall = (Button) findViewById(R.id.btn_end);
        btnSpeaker = (CheckBox) findViewById(R.id.btn_speaker);
        tvSignal = (TextView) findViewById(R.id.tv_signal);
        ivSignal = (ImageView) findViewById(R.id.iv_signal);
        tvLog = (TextView)findViewById(R.id.tv_log);
        //控制机器人音量
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        btnCamera = (CheckBox) findViewById(R.id.btn_camera);
        btnMic = (CheckBox) findViewById(R.id.btn_mic);
        rlControl = (RelativeLayout) findViewById(R.id.rl_control);
        btnEndCall.setVisibility(View.VISIBLE);
    }

    private void calculateWidth() {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        choicesView.measure(w, h);
        head_control_layout.measure(w, h);
        choicesViewWidth = choicesView.getMeasuredWidth();
        headControlWidth = head_control_layout.getMeasuredWidth();
    }

    private void changeCamera() {
        if (bCameraEnable) {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
            avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
        } else {
            ILVCallManager.getInstance().enableCamera(mCurCameraId, true);
        }
        bCameraEnable = !bCameraEnable;
    }

    private void changeMic() {
        if (bMicEnalbe) {
            ILVCallManager.getInstance().enableMic(false);
        } else {
            ILVCallManager.getInstance().enableMic(true);
        }

        bMicEnalbe = !bMicEnalbe;
    }

    private void changeSpeaker() {
        if (bSpeaker) {
            ILVCallManager.getInstance().enableSpeaker(false);
        } else {
            ILVCallManager.getInstance().enableSpeaker(true);
            ILiveSDK.getInstance().getAvAudioCtrl().setAudioOutputMode(AVAudioCtrl.OUTPUT_MODE_SPEAKER);
        }
        bSpeaker = !bSpeaker;
        if (isMonitor) {
            IMService.getInstance().sendMessage("callOpenMic:"+bSpeaker,callNumber);
        }
    }

    private void switchCamera() {
        mCurCameraId = (ILiveConstants.FRONT_CAMERA == mCurCameraId) ? ILiveConstants.BACK_CAMERA : ILiveConstants.FRONT_CAMERA;
        System.out.println("mCurCameraId===="+mCurCameraId);
        System.out.println("CamID1===="+ILVCallManager.getInstance().getCurCameraId());
        ILVCallManager.getInstance().switchCamera(mCurCameraId);
        System.out.println("currentCamID2===="+ILVCallManager.getInstance().getCurCameraId());
    }

    private void setBeauty() {
        if (null == sbBeauty) {
            sbBeauty = (SeekBar) findViewById(R.id.sb_beauty_progress);
            sbBeauty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                    Toast.makeText(CallActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    // TODO Auto-generated method stub
                    mBeautyRate = progress;
                    avVideoCtrl.inputBeautyParam(9.0f * progress / 100.0f);
                }
            });
        }
        llBeauty.setVisibility(View.VISIBLE);
        rlControl.setVisibility(View.INVISIBLE);
    }

    private void showInviteDlg(){
        final EditText etInput = new EditText(this);
        new AlertDialog.Builder(this).setTitle("")
                .setView(etInput)
                .setPositiveButton("", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(etInput.getText().toString())) {
                            List<String> nums = new ArrayList<String>();
                            nums.add(etInput.getText().toString());
                            ILVCallManager.getInstance().inviteUser(mCallId, nums);
                        }
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
    // 注册广播
    private void intiReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("com.reeman.showCallVolume");
//        intentFilter.addAction(WeakNetWorkDialogFragment.WEAK_NETWORK_CANCLE);
//        intentFilter.addAction(WeakNetWorkDialogFragment.WEAK_NETWORK_OK);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }
    private void initListener() {
        ILVCallManager.getInstance().addCallListener(this);
        head_down.setOnTouchListener(this);
        head_up.setOnTouchListener(this);
        head_left.setOnTouchListener(this);
        head_right.setOnTouchListener(this);
    }
//拨电话前的计时器，当过去两秒后认为是用户真心想打电话
    Timer callOutTimer = new Timer();
    //记录是否播出电话，如果在没有打通电话的情况下按了挂断按钮，停止callOutTimer
    private boolean callSuccess = false;
    //判断是否是是监控小曼，如果是则不用监听小曼的心跳。
    public boolean isMonitor=false;
    private void initCall() {
        Intent intent = getIntent();
        reemanListener = ReeManSDKManager.getInstance().getListener();
        mHostId = intent.getStringExtra("HostId");
        mCallType = intent.getIntExtra("CallType", ILVCallConstants.CALL_TYPE_VIDEO);
        mCallId = intent.getIntExtra("CallId", 0);
        videoType = intent.getIntExtra("type", 0);
        callTip = intent.getStringExtra("callTip");
        head_control_layout.setVisibility(View.VISIBLE);
        if (videoType == 1) {//1代表监控
            isMonitor=true;
            option = new ILVCallOption(mHostId)
                    .callTips("Monitor")
                    .setMemberListener(this)
                    .autoSpeaker(true)
                    .heartBeatInterval(10)
                    .setOnlineCall(true)
                    .setCallType(mCallType);
            ILVCallManager.getInstance().enableCamera(mCurCameraId, false);
            avRootView.closeUserView(ILiveLoginManager.getInstance().getMyUserId(), AVView.VIDEO_SRC_TYPE_CAMERA, true);
            btn_beauty.setVisibility(View.GONE);
            ll_volume.setVisibility(View.GONE);
            btn_charge.setVisibility(View.VISIBLE);
            btnMic.setChecked(true);
        } else {
            isMonitor = false;
            option = new ILVCallOption(mHostId)
                    .callTips("Video,abc,"+callTip)//匹配规则，video代表视频请求，abc字段是判断是否为主人，如果是则是admin;最后参数代表昵称，就是对方收到来电时显示的
                    .autoSpeaker(true)
                    .autoMic(true)
                    .setMemberListener(this)
                    .cameraId(ILiveConstants.FRONT_CAMERA)
                    .setOnlineCall(true)
                    .setCallType(mCallType);
            btn_beauty.setVisibility(View.VISIBLE);
            btn_charge.setVisibility(View.GONE);
        }
        if (0 == mCallId) { // 发起呼叫
            final List<String> nums = intent.getStringArrayListExtra("CallNumbers");
            requestCallView.setVisibility(View.VISIBLE);
            if (nums.size() > 1){
                mCallId = ILVCallManager.getInstance().makeMutiCall(nums, option);
            }else{
                callOutTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        callNumber = nums.get(0);
                        mCallId = ILVCallManager.getInstance().makeCall(callNumber, option, new ILiveCallBack() {
                            @Override
                            public void onSuccess(Object data) {
                                System.out.println("发起成功"+data);
                                if (reemanListener != null) {
                                    reemanListener.onCalling("发起成功");
                                }
                                callSuccess = true;
                            }
                            @Override
                            public void onError(String module, final int errCode, final String errMsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (6012 == errCode) {
                                            if (reemanListener!=null)
                                            reemanListener.onCalling("请求服务器超时,请重新拨打或者检查您的网络连接");
                                            SingleToastUtils.getSingleToastLong(CallActivity.this, "请求服务器超时,请重新拨打或者检查您的网络连接");
                                        } else if (errCode==10004){
                                            if (reemanListener!=null)
                                            reemanListener.onCalling("房间获取失败，请重新拨打或重新启动应用");
                                            SingleToastUtils.getSingleToastLong(CallActivity.this, "房间获取失败，请重新拨打或重新启动应用");
                                            IMService.getInstance().logout();
                                            IMService.getInstance().stopSelf();
                                            startService(new Intent(CallActivity.this, IMService.class));
                                        } else {
                                            SingleToastUtils.getSingleToastLong(CallActivity.this, "通话发起失败，错误码："+errCode);
                                        }
                                    }
                                });
                                if (reemanListener!=null)
                                reemanListener.onCalling("发起失败，errCode:"+errCode+";msg:"+errMsg);
                                System.out.println("发起失败：module："+module+"/errCode:"+errCode+"/errMsg:"+errMsg);
                            }
                        });
                    }
                },1500);
                playRing();
            }
            requestCallView.setVisibility(View.VISIBLE);
            isCallIn = false;
        }else{  // 接听呼叫
            isCallIn = true;
            callNumber = mHostId;
            requestCallView.setVisibility(View.GONE);
            ILVCallManager.getInstance().acceptCall(mCallId, option);
        }

        avRootView.setAutoOrientation(false);
        avRootView.layoutVideo(false);
        ILVCallManager.getInstance().initAvView(avRootView);
    }
//解决锁屏时接收到来电接听后画面扭曲的问题
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        width = defaultDisplay.getWidth();
        height = defaultDisplay.getHeight();
        System.out.println("CallActivity:onConfigurationChanged/(width,height)="+"("+width+","+height+")");
        invalidCallUi();
        super.onConfigurationChanged(newConfig);
    }

    private void invalidCallUi() {
        System.out.println("invalidCall");
        if (mainPicture != null) {
            System.out.println("mainPicture!=null");
            mainPicture.setPosHeight(height);
            mainPicture.setPosWidth(width);
            mainPicture.invalidate();
        } else {
            System.out.println("mainPicture==null");
        }
        if (subPicture != null) {
            System.out.println("subPicture！=null");
            subPicture.setPosWidth(480);
            subPicture.setPosHeight(270);
            subPicture.invalidate();
        }
    }

    private ImageView video_robot_tou;
    private void playRing() {
        shakeHeadAnim = AnimationUtils.loadAnimation(this, R.anim.yaobait);
        video_robot_tou.startAnimation(shakeHeadAnim);
        initPlayer();
    }
    private void stopRing() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            }
            player = null;
        }
    }
    MediaPlayer player;
    private void initPlayer() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.outgoing);
        }
        player.setLooping(true);
        player.start();
    }
    //判断是否是来电，为了调试camera，因为有些情况下接电话后相机没有打开。
    private boolean isCallIn=false;
    @Override
    protected void onResume() {
        if (floatService != null) {
            System.out.println("float:停止服务");
            callTime = CallWindowManager.getInstance().getTime();
            System.out.println("float:计算时间：：" + callTime);
            stopService(floatService);
            floatService = null;
            timeConsume();
        }
        ILVCallManager.getInstance().initAvView(avRootView);
        System.out.println("CallActivity : onResume");
        ILVCallManager.getInstance().onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        System.out.println("CallActivity : onPause");
        ILVCallManager.getInstance().onPause();
//        if (!listenHeartCancled) {
//            System.out.println("float：启动悬浮窗服务");
//            timeConsumeTask.cancel();
//            timeConsumeTask = null;
//            floatService = new Intent(this, CallFloatWindowService.class);
//            floatService.putExtra("time", callTime);
//            floatService.putExtra("callType", isMonitor);
//            startService(floatService);
//        }
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        ILVCallManager.getInstance().endCall(mCallId);
//        moveTaskToBack(true);
    }

    private int seekBarProgress = 1;
    private long preTime=0;
    @Override
    public void onClick(View v) {
        int clickID = v.getId();
        if (clickID == R.id.video_main_weijitong_guaduan) {
            ILVCallManager.getInstance().endCall(mCallId);
        }
        //不让用户频繁的按视频通话页面的按键
        if (preTime == 0) {
            preTime = (new Date()).getTime();
            switches(clickID);
        } else {
            long curTime = (new Date()).getTime();
            long delTime = curTime-preTime;
            if(delTime<1500){
                if (clickID==R.id.btn_mic){
                    btnMic.setChecked(btnMic.isChecked()==false?true:false);
                }
                else if (clickID==R.id.btn_camera){
                    btnCamera.setChecked(btnCamera.isChecked()==false?true:false);
                } else if (clickID==R.id.btn_speaker) {
                    btnSpeaker.setChecked(btnSpeaker.isChecked() == false ? true : false);
                }
                SingleToastUtils.getSingleToast(this, "频率不要太快噢，亲");
            }else{
                switches(clickID);
                preTime=0;
            }
            preTime=curTime;
        }
    }

    private void switches(int clickID) {
        // library中不能使用switch索引资源id
        if (clickID == R.id.btn_end){
            ILVCallManager.getInstance().endCall(mCallId);
            //发送此条消息是为了防止挂断电话后机器端没有挂断然后卡死，机器端接收到消息后在心跳中判断；
            IMService.getInstance().sendOnlyCode("remote_cancel",callNumber);
        }else if (clickID==R.id.video_main_weijitong_guaduan){
            if (!callSuccess) {
                System.out.println("取消呼叫");
                callOutTimer.cancel();
                stopRing();
                finish();
                callSuccess = false;
            }
        }else if (clickID == R.id.btn_camera){
            changeCamera();
        }else if(clickID == R.id.btn_mic){
            changeMic();
        }else if(clickID == R.id.btn_switch_camera){
            switchCamera();
        }else if(clickID == R.id.btn_speaker){
            changeSpeaker();
        }else if(clickID == R.id.btn_beauty){
            setBeauty();
        }else if(clickID == R.id.btn_beauty_setting_finish){
            llBeauty.setVisibility(View.GONE);
            rlControl.setVisibility(View.VISIBLE);
        }else if(clickID == R.id.btn_invite){
            showInviteDlg();
        }else if(clickID == R.id.btn_log){
            if (View.VISIBLE == tvLog.getVisibility()){
                tvLog.setVisibility(View.GONE);
            }else{
                tvLog.setVisibility(View.VISIBLE);
            }
        } else if (clickID == R.id.seekbar) {
            if (!seekBar.isShown()) {
                seekBar.setProgress(seekBarProgress);
                seekBar.setVisibility(View.VISIBLE);
            } else {
                seekBar.setVisibility(View.GONE);
            }
        } else if (clickID == R.id.btn_charge) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("警告").setMessage("充电命令发出后，此次监控将会结束，是否继续？")
                    .setCancelable(false);
            builder.setNegativeButton("继续发送", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IMService.getInstance().sendMessage("Subs_charge_quick",callNumber);
                    ILVCallManager.getInstance().endCall(mCallId);
                }
            });
            builder.setPositiveButton("取消发送", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }
    Timer sendHeartBeatTimer;
    Timer sendMonitorBeatTimer;
    Timer listenHeartBeatTimer;

    /**
     * 会话建立回调
     * @param callId
     */
    @Override
    public void onCallEstablish(int callId) {
        reemanListener.onCallEstablished(callId);
        System.out.println("oncall established");
        //发送头部回正消息
        IMService.getInstance().sendOnlyCode("callback_back",callNumber);
        IMService.receivedCall = false;
        stopRing();
        if (!isMonitor) {
            sendCustomHeartBeat();
        } else {
            sendMonitorHeartBeat();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ILVCallManager.getInstance().enableMic(false);
                    ILVCallManager.getInstance().enableSpeaker(true);
                    bMicEnalbe = false;
                }
            });
        }
        listenHeartBeat();
//        timeConsume();
        requestCallView.setVisibility(View.GONE);
        callMainView.setVisibility(View.VISIBLE);
        btnEndCall.setVisibility(View.VISIBLE);
        avRootView.swapVideoView(0, 1);
        verifyCallTypeAndRotate();
        // 设置点击小屏切换及可拖动
        dragAndSwitch();
        debugCam();
    }
    private int callTime=0;
    TimerTask timeConsumeTask;
    private void timeConsume() {
        timeConsumeTask =new TimerTask() {
            @Override
            public void run() {
                callTime++;
            }
        };
        listenHeartBeatTimer.schedule(timeConsumeTask,0,1000);
    }
    public boolean isOnline = false;
    private void listenHeartBeat() {
        listenHeartBeatTimer=new Timer();
        listenHeartBeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isOnline) {
                    //在线
                    System.out.println("对方在线---------------");
                    isOnline = false;
                } else {
                    //离线

                    System.out.println("对方离线---------------");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SingleToastUtils.getSingleToast(CallActivity.this,"对方网络出错");
                            if (!MessageManager.callEnded) {
                                ILVCallManager.getInstance().endCall(mCallId);
                            } else {
                                finish();
                            }
                            MessageManager.callEnded = false;
                        }
                    });
                    if (!listenHeartCancled) {
                        cancel();
                        listenHeartCancled = false;
                    }
                }
            }
        },10000,10000);
    }
    private void sendCustomHeartBeat() {
        sendHeartBeatTimer=new Timer();
        sendHeartBeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("手机端在发心跳");
                System.out.println("callNumber:::"+callNumber);
                IMService.getInstance().sendOnlyCode("calling",callNumber);
            }
        },0,3000);
    }

    private void sendMonitorHeartBeat() {
        sendMonitorBeatTimer = new Timer();
        sendMonitorBeatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("手机端在发心跳");
                System.out.println("monitoring:::"+callNumber);
                IMService.getInstance().sendOnlyCode("monitoring",callNumber);
            }
        },0,3000);
    }
    BroadcastReceiver callReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IMService.CALL_IS_RUNING)) {
                isOnline=true;
            }
        }
    };
    private void initCallHeart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(IMService.CALL_IS_RUNING);
        registerReceiver(callReceiver,filter);
    }
    private void debugCam() {
        if (isCallIn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(500);
                    for (int i=0;i<2;i++) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    switchCamera();
                                }
                        });
                        SystemClock.sleep(800);
                    }
                }
            }).start();
        }
    }

    private void dragAndSwitch() {
        for (int i = 1; i< ILiveConstants.MAX_AV_VIDEO_NUM; i++){
            final int index = i;
            AVVideoView minorView = avRootView.getViewByIndex(i);
            if (ILiveLoginManager.getInstance().getMyUserId().equals(minorView.getIdentifier())){
                minorView.setMirror(true);      // 本地镜像
            }
            minorView.setDragable(true);// 小屏可拖动
            minorView.setGestureListener(new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    avRootView.swapVideoView(0, index);     // 与大屏交换
                    return false;
                }
            });
        }
    }

    private void getLossAndDelayThread() {
        lossAndDelay = new Intent(this, ComputeLossAndDelayService.class);
        bindService(lossAndDelay,conn, Context.BIND_AUTO_CREATE);
    }

    private void verifyCallTypeAndRotate() {
        if (videoType == 1) {
            ILVCallManager.getInstance().enableMic(false);
            changeCamera();
        } else {
            ILVCallManager.getInstance().enableMic(true);
            ILVCallManager.getInstance().enableCamera(ILiveConstants.FRONT_CAMERA,true);
        }
        mainPicture = avRootView.getViewByIndex(0);
        subPicture = avRootView.getViewByIndex(1);
        if (mainPicture != null) {
            mainPicture.setDiffDirectionRenderMode(BaseVideoView.BaseRenderMode.BLACK_TO_FILL);
            mainPicture.setRotate(false);
            mainPicture.setOnTouchListener(this);
            mainPicture.setRotation(90);
            mainPicture.setBackground(BitmapFactory.decodeResource(getResources(), R.mipmap.dormant_status));
        }
        if (subPicture != null) {
            subPicture.setDiffDirectionRenderMode(BaseVideoView.BaseRenderMode.BLACK_TO_FILL);
            subPicture.setRotate(false);
            subPicture.setRotation(90);
            subPicture.setBackground(BitmapFactory.decodeResource(getResources(), R.mipmap.subview_background));
        }
    }

    /**
     * 会话结束回调
     *
     * @param callId
     * @param endResult 结束原因
     * @param endInfo   结束描述
     */
    private boolean listenHeartCancled = false;
    @Override
    public void onCallEnd(int callId, int endResult, String endInfo) {
        reemanListener.onCallEnd(endInfo,endResult);
        stopRing();
        if (listenHeartBeatTimer != null) {
            listenHeartBeatTimer.cancel();
            listenHeartBeatTimer = null;
        }
        listenHeartCancled = true;
        dismissWeakNetDialog();
        if (endResult == ILVCallConstants.ERR_CALL_SPONSOR_TIMEOUT) {
            SingleToastUtils.getSingleToast(CallActivity.this,"暂时无人接听，请稍后再播");
        } else if (endResult == ILVCallConstants.ERR_CALL_RESPONDER_REFUSE) {
            SingleToastUtils.getSingleToast(CallActivity.this,"对方暂时不方便接听电话，请稍后再播");
        }else if (endResult == ILVCallConstants.ERR_CALL_HANGUP && endInfo.contains("Remote cancel")) {
            SingleToastUtils.getSingleToast(CallActivity.this,"对方挂断电话");
        } else if (endResult == ILVCallConstants.ERR_CALL_RESPONDER_LINEBUSY) {
            SingleToastUtils.getSingleToast(CallActivity.this,"对方正在通话中");
        } else if (endResult == ILVCallConstants.ERR_CALL_NOT_EXIST) {
            SingleToastUtils.getSingleToast(CallActivity.this,"通话不存在");
        } else if (endResult != ILVCallConstants.ERR_CALL_HANGUP) {
        } else if (endResult != ILVCallConstants.ERR_CALL_SPONSOR_CANCEL) {
        } else {
            SingleToastUtils.getSingleToast(CallActivity.this, "结束结果=" + endResult);
        }
        System.out.println("melon>>>>>onendcall:::::::::::callId:"+callId+"/endResult:"+endResult+"/endInfo:"+endInfo);
        finish();
    }

    @Override
    public void onException(int iExceptionId, int errCode, String errMsg) {
        System.out.println("melon>>>>>CallAcitivy>>>>>>>>onException:::::::::::iExceptionId:"+iExceptionId+"/errCode:"+errCode+"/errMsg:"+errMsg);
    }

    @Override
    public void onCameraEvent(String id, boolean bEnable) {
        addLogMessage("["+id+"] "+(bEnable?"open":"close")+" camera");
    }

    @Override
    public void onMicEvent(String id, boolean bEnable) {
        addLogMessage("["+id+"] "+(bEnable?"open":"close")+" mic");
    }
    @Override
    public void onMemberEvent(String id, boolean bEnter) {
        System.out.println("melon>>>>>>>>["+id+"] "+(bEnter?"join":"exit"));
    }

    Timer lossAndDelayTimer;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("melon>>>>>延迟服务连接成功");
            listener= (ComputeLossAndDelayService.OnLossAndDelayListener) service;
            final ComputeLossAndDelayService computeLossAndDelayService = listener.getService();
            lossAndDelayTimer=new Timer();
            lossAndDelayTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String result = computeLossAndDelayService.start("ping -c 4 " + "113.105.73.148");//腾讯
                    if (result.length()<1) {
                        result = "10";
                    }
                    final int delay = Integer.parseInt(result);
                    //TODO changeUI
                    try {
                        changeSignalUI(delay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },10,5000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("melon：：：onServiceDisconnected："+name.toString());
        }
    };

    private void changeSignalUI(final int delay) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (delay <= 100) {
                    //延迟低 满格
                    tvSignal.setText(delay+"ms");
                    tvSignal.setTextColor(getResources().getColor(R.color.green));
                    ivSignal.setImageResource(R.mipmap.signal6);
                } else if (delay>100&&delay<=300) {
                    //少一格
                    tvSignal.setText(delay+"ms");
                    tvSignal.setTextColor(getResources().getColor(R.color.signalOrange));
                    ivSignal.setImageResource(R.mipmap.signal5);
                }else if (delay>300) {
                    //再少一格
                    tvSignal.setText(delay+"ms");
                    tvSignal.setTextColor(getResources().getColor(R.color.red));
                    ivSignal.setImageResource(R.mipmap.signal4);
                    if (isFirstShow) {
//                        if (ActivityCollector.ViewisFront("com.customcall.CallActivity")) {
//                            showWeakNetworDialog();
//                            isFirstShow = false;
//                        }
                        SingleToastUtils.getSingleToast(CallActivity.this,"当前网络较差");
                        isFirstShow = false;
                    }
                }
            }
        });
    }
    private boolean isFirstShow = true;
    private android.support.v7.app.AlertDialog.Builder builder;
    private android.support.v7.app.AlertDialog weakNetworkDialog;
    private void showWeakNetworDialog() {
        builder = new android.support.v7.app.AlertDialog.Builder(CallActivity.this);
        builder.setIcon(R.mipmap.robot_icon);
        builder.setTitle("网络异常");
        builder.setMessage("网络太差，请稍后重试！");
        builder.setPositiveButton("取消通话", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissWeakNetDialog();
                ILVCallManager.getInstance().endCall(mCallId);
            }
        });
        builder.setNegativeButton("继续通话", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissWeakNetDialog();
            }
        });
        weakNetworkDialog = builder.create();
        weakNetworkDialog.setCanceledOnTouchOutside(false);
        weakNetworkDialog.show();
    }
    private void dismissWeakNetDialog() {
        if (weakNetworkDialog != null) {
            weakNetworkDialog.dismiss();
            weakNetworkDialog = null;
        }
    }
    //robot_control.isChecked()为真时控制身体，假时控制头部
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int touchID = v.getId();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                controlRobot(touchID);
                changeButtonUI(true,touchID);
                break;
            case MotionEvent.ACTION_UP:
                if (robot_control.isChecked()) {
                    IMService.getInstance().sendMessage("0|0",callNumber);
                }
                changeButtonUI(false, touchID);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    private void changeButtonUI(boolean pressed, int touchID) {
        if (touchID == R.id.head_down) {
            head_down.setBackgroundResource(pressed ? R.drawable.video_down_tint_bitmap : R.mipmap.video_down);
        } else if (touchID == R.id.head_right) {
            head_right.setBackgroundResource(pressed ? R.drawable.video_right_tint_bitmap : R.mipmap.video_right);
        } else if (touchID == R.id.head_up) {
            head_up.setBackgroundResource(pressed ? R.drawable.video_up_tint_bitmap : R.mipmap.video_up);
        } else if (touchID == R.id.head_left) {
            head_left.setBackgroundResource(pressed ? R.drawable.video_left_tint_bitmap : R.mipmap.video_left_);
        }
    }

    private void controlRobot(int touchID) {
        //robot_control.isChecked()为真时控制身体，假时控制头部
        if (robot_control.isChecked()) {
            if (touchID == R.id.head_down) {
                IMService.getInstance().sendMessage("2|255",callNumber);
            } else if (touchID == R.id.head_right) {
                IMService.getInstance().sendMessage("4|180",callNumber);
            } else if (touchID == R.id.head_up) {
                IMService.getInstance().sendMessage("1|255",callNumber);
            } else if (touchID == R.id.head_left) {
                IMService.getInstance().sendMessage("3|180",callNumber);
            }
        } else {
            if (touchID == R.id.head_down) {
                IMService.getInstance().sendMessage("headdown",callNumber);
            } else if (touchID == R.id.head_right) {
                IMService.getInstance().sendMessage("headleft",callNumber);
            } else if (touchID == R.id.head_up) {
                IMService.getInstance().sendMessage("headup",callNumber);
            } else if (touchID == R.id.head_left) {
                IMService.getInstance().sendMessage("headright",callNumber);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        IMService.getInstance().sendMessage("volume:" + seekBar.getProgress(),callNumber);
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("AVProgress", "action==" + action);
            exeBroadcast(intent, action);
        }
    };

    private void exeBroadcast(Intent intent, String action) {
        if (action.equals("com.reeman.showCallVolume")) {
            seekBarProgress = intent.getIntExtra("Volume", 0);
            System.out.println("zhangsan   收到robot当前电话音量   seekBarProgress: " + seekBarProgress);
            if (seekBar != null) {
                seekBar.setProgress(seekBarProgress);
            }
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (!NetUtil.isNetworkAvailable(CallActivity.this)) {
                SingleToastUtils.getSingleToast(CallActivity.this,"网络断开，退出通话");
                finish();
            }
        }
    }

    //处理视频通话时主屏的touch事件,作为隐藏上层页面按键的点击事件
    private boolean animated = false;
    @Override
    public boolean onTouch(GLView glView, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (!isMonitor) {
//                    head_control_layout.setVisibility(head_control_layout.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
//                    choicesView.setVisibility(choicesView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    ll_volume.setVisibility(ll_volume.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//                    head_control_layout.setVisibility(head_control_layout.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
                }
                AnimUtils.transAnim(animated,head_control_layout,-headControlWidth);
                AnimUtils.transAnim(animated,choicesView,choicesViewWidth);
                animated = !animated;
//                btnEndCall.setVisibility(btnEndCall.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (floatService != null) {
            System.out.println("float:关闭悬浮窗服务");
            stopService(floatService);
        }
        System.out.println("CallActivity : onDestroy");
        dismissWeakNetDialog();
        MessageManager.callEnded = false;
        if (lossAndDelayTimer!=null) {
            lossAndDelayTimer.cancel();
            lossAndDelayTimer = null;
        }
        unbindService(conn);
        conn = null;
        if (sendHeartBeatTimer != null) {
            sendHeartBeatTimer.cancel();
            sendHeartBeatTimer = null;
        }
        if (sendMonitorBeatTimer != null) {
            sendMonitorBeatTimer.cancel();
            sendMonitorBeatTimer = null;
        }
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(callReceiver);
        mBroadcastReceiver = null;
        callReceiver = null;
        ILVCallManager.getInstance().removeCallListener(this);
        ILVCallManager.getInstance().onDestory();
        ILVCallManager.getInstance().shutdown();//资源释放
        System.gc();
        startService(new Intent(this, IMService.class));
//        startActivity(new Intent(this, MainActivity.class));
        super.onDestroy();
    }
}
