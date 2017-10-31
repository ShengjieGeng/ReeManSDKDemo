# ReeManSDKDemo
a smart home for everyone
在app的gradle中添加 compile 'compile com.melon.reeman:reemansdk:2.1.0'
一、在工程目录下的application类中初始化reeman 
    ReeManApplication.getInstance(getApplicationContext()).initReemanApplication();
二、添加需要的监听事件
    1. ReeManSDKManager.getInstance().addCallListener(this);
    2. ReeManSDKManager.getInstance().addStatusListener(this);
    3. ReeManSDKManager.getInstance().addMessageListener(this);
三、注册账号
    ReeManSDKManager.getInstance().registerReeman(number, pass,ReemanRegisteListener)
四、登录
    ReeManSDKManager.getInstance().loginReeman(number, pass,ReemanLoginListener)
五、视频和监控
    /**
     * @param context
     * @param callNumber 拨打的电话号码
     * @param type 通话类型：TYPE_VIDEO,TYPE_MONITOR
     * @param tip 通话提示，用来在机器端的来电显示
     */
    ReeManSDKManager.getInstance().call(getApplicationContext(), callNumber, type,tip);
六、发消息
    messgeSender = ReeManSDKManager.getInstance().getMessgeSender()
    messgeSender.sendMessage(params);
七、退出
    ReeManSDKManager.getInstance().onDestroy();
