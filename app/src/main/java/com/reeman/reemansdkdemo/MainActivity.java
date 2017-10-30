package com.reeman.reemansdkdemo;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.reeman.reemansdk.ReeManSDKManager;
import com.reeman.reemansdk.interfaces.ReemanLoginListener;
import com.reeman.reemansdk.interfaces.ReemanMessageListener;
import com.reeman.reemansdk.interfaces.ReemanRegisteListener;
import com.reeman.reemansdk.interfaces.ReemanCallListener;
import com.reeman.reemansdk.interfaces.ReemanUserStatusListener;
import com.reeman.reemansdk.service.IMService;
import com.reeman.reemansdk.utils.SPManager;
import com.reeman.reemansdk.utils.message.ReemanMessage;
import com.reeman.reemansdk.utils.message.ReemanMessageSender;
import com.reeman.reemansdk.utils.message.TencentMessage;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements ReemanCallListener, ReemanUserStatusListener, ReemanMessageListener {

    private TextView log;
    String TAG = "reemancallsdk-";
    private ReemanMessageSender messgeSender;
    private String path;
    private EditText id_username;
    private EditText id_password;
    private EditText callNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log = (TextView) findViewById(R.id.log1);
        String [] strings = new String[]{
                Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
        queryMorePermiss(strings, "是否打开相应权限", 0, new PermissListener() {
            @Override
            public void permissSuccess() {

            }
        });
        id_username = (EditText) findViewById(R.id.id_username);
        id_password = (EditText) findViewById(R.id.id_password);
        callNumber = (EditText) findViewById(R.id.callNumber);
        ReeManSDKManager.getInstance().addCallListener(this);
        ReeManSDKManager.getInstance().addStatusListener(this);
        ReeManSDKManager.getInstance().addMessageListener(this);
        stringBuilder = new StringBuilder("");
        messgeSender = ReeManSDKManager.getInstance().getMessgeSender();
    }
    View focusView = null;
    public void regist(View view) {
        id_password.setError(null);
        id_username.setError(null);
        ReeManSDKManager.getInstance().registerReeman(id_username.getText().toString().trim()
                , id_password.getText().toString().trim(), new ReemanRegisteListener() {
            @Override
            public void onError(String msg) {
                System.out.println(TAG+"regist onError"+msg);
                id_username.setError(msg);
                focusView = id_username;
                focusView.requestFocus();
            }

            @Override
            public void onSuccess() {
                System.out.println(TAG+"regist onSuccess");
            }
        });
    }

    public void share(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent,0);
    }

    public void sharePic(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {

            try {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "rw");
                FileInputStream fis = videoAsset.createInputStream();
                path = Environment.getExternalStorageDirectory() + "/VideoFile.mp4";
                File tmpFile = new File(path);
                FileOutputStream fos = new FileOutputStream(tmpFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
            } catch (Exception io_e) {
                // TODO: handle error
            }
            System.out.println(TAG+"PATH=="+path);
            messgeSender.sendMessage(ReemanMessage.MESSAGE_VIDEO
                    , "/storage/emulated/0/DCIM/Camera/VID_20171030_091901.mp4"
                    , SPManager.getSharedPreferences(MainActivity.this).getString("deviceID", ""), new TencentMessage.MessageListener() {

                @Override
                public void onSendingSuccess() {
                    System.out.println(TAG+"onSendingSuccess");
                }

                @Override
                public void onSendingError(String msg) {
                    System.out.println(TAG+"onSendingError:"+msg);
                }
            });
            System.out.println("url:"+data.getData());
            System.out.println("url2:"+data.getDataString());
        }
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null,null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                System.out.println(TAG + "path=" + path);
                messgeSender.sendMessage(ReemanMessage.MESSAGE_PICTURE, path, SPManager.getSharedPreferences(this).getString("deviceID", ""), new TencentMessage.MessageListener() {

                    @Override
                    public void onSendingSuccess() {
                        System.out.println(TAG+"onSendingSuccess");
                    }

                    @Override
                    public void onSendingError(String msg) {
                        System.out.println(TAG+"onSendingError:"+msg);
                    }
                });
            }
        }
    }
    public void call(View view) {
        callNumber.setError(null);
        String num = callNumber.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            callNumber.setError("号码为空");
            focusView = callNumber;
            focusView.requestFocus();
        } else {
            System.out.println(TAG+"MainActivity:callNumber==="+callNumber);
            ReeManSDKManager.getInstance().call(getApplicationContext(), num, ReeManSDKManager.TYPE_VIDEO);
        }
    }
    public void monitor(View view) {
        callNumber.setError(null);
        String num = callNumber.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            callNumber.setError("号码为空");
            focusView = callNumber;
            focusView.requestFocus();
        } else {
            stringBuilder = new StringBuilder("");
            System.out.println(TAG+"MainActivity:callNumber==="+callNumber);
            ReeManSDKManager.getInstance().call(getApplicationContext(), num, ReeManSDKManager.TYPE_MONITOR);
        }
    }
    public void login(View view) {
        id_username.setError(null);
        id_password.setError(null);
        ReeManSDKManager.getInstance().loginReeman(id_username.getText().toString().trim()
                , id_password.getText().toString().trim(), new ReemanLoginListener() {
            @Override
            public void onError(final String msg) {
                System.out.println(TAG+"melon onError:::::msg=="+msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    id_username.setError(msg);
                    focusView = id_username;
                    focusView.requestFocus();
                    }
                });
            }

            @Override
            public void onSuccess(String sig) {
                log.setText("login onSuccess");
                System.out.println(TAG+"melon onSuccess:::::sig=="+sig);
            }
        });
    }
    public void queryMorePermiss(String[] permisses, String permiss_desc, int permiss_code, PermissListener listener) {
        if (EasyPermissions.hasPermissions(this, permisses)) {
            listener.permissSuccess();

        } else {
            EasyPermissions.requestPermissions(this, permiss_desc,
                    permiss_code, permisses);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stringBuilder!=null) {
            log.setText(stringBuilder.toString());
        }
    }

    @Override
    protected void onDestroy() {
        ReeManSDKManager.getInstance().onDestroy();
        super.onDestroy();
    }

    StringBuilder stringBuilder;
    @Override
    public void onCallEnd(String msg, int endCode) {
        System.out.println(TAG+"MainActivity:onCallEnd");
        stringBuilder.append("\n");
        stringBuilder.append("MainActivity:onCallEnd");
    }

    @Override
    public void onCallEstablished(int callId) {
        System.out.println(TAG+"MainActivity:onCallEstablished");
        stringBuilder.append("\n");
        stringBuilder.append("MainActivity:onCallEstablished");
    }

    @Override
    public void onCalling(String msg) {
        System.out.println(TAG+"MainActivity:onCalling");
        stringBuilder.append("\n");
        stringBuilder.append("MainActivity:onCalling");
    }

    @Override
    public void onForceLogout(String msg) {
        System.out.println(TAG+msg);
    }

    @Override
    public void onReceiveMessage(TIMMessage timMessage) {
        long elementCount = timMessage.getElementCount();
        System.out.println(TAG + "count=" + elementCount);
        for (int i=0;i<elementCount;i++) {
            TIMElem element = timMessage.getElement(i);
            TIMElemType type = element.getType();
            System.out.println(TAG+i+"type="+type);
        }
    }
}
