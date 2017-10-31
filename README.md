# ReeManSDKDemo
## a smart home for everyone
<br/>在app的gradle中添加 
```Java
compile 'compile com.melon.reeman:reemansdk:2.1.0'
```
### 一、在工程目录下的application类中初始化reeman 
   ```Java
   ReeManApplication.getInstance(getApplicationContext()).initReemanApplication();
   ```
### 二、添加需要的监听事件
```Java
    ReeManSDKManager.getInstance().addCallListener(this);
```
```Java 
    ReeManSDKManager.getInstance().addStatusListener(this);
```
```Java 
    ReeManSDKManager.getInstance().addMessageListener(this);
```
### 三、注册账号
  
 ```Java 
    ReeManSDKManager.getInstance().registerReeman(number, pass,ReemanRegisteListener)
 ```
### 四、登录
```Java 
    ReeManSDKManager.getInstance().loginReeman(number, pass,ReemanLoginListener)
```
### 五、视频和监控
```Java 
    ReeManSDKManager.getInstance().call(getApplicationContext(), callNumber, type,tip); 
```
### 六、发消息
```java
    messgeSender = ReeManSDKManager.getInstance().getMessgeSender()
    messgeSender.sendMessage(params);
```
### 七、退出
```java
    ReeManSDKManager.getInstance().onDestroy();
```
### 上图
![reeman](https://github.com/ShengjieGeng/ReeManSDKDemo/blob/master/reemanDemo.png)
