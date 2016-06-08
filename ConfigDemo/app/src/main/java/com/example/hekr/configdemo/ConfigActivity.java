package com.example.hekr.configdemo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.hekr.hekrsdk.util.FindDeviceBean;
import me.hekr.hekrsdk.util.HekrConfig;
import me.hekr.hekrsdk.util.UIDscDevUtil;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView ssid;
    private EditText pwd_input;
    private BroadcastReceiver connectionReceiver;
    private WifiManager.MulticastLock lock = null;

    private HekrConfig hekrConfig;
    private UIDscDevUtil uiDscDevUtil;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        initView();
        initData();
    }

    private void initView() {
        ssid = (TextView) findViewById(R.id.ssid);
        pwd_input = (EditText) findViewById(R.id.pwd_input);
        Button connect = (Button) findViewById(R.id.device_connect_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在配网...");
        progressDialog.setCanceledOnTouchOutside(false);

        if (connect != null) {
            connect.setOnClickListener(this);
        }
    }

    private void initData() {
        createReceiver();

        WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (lock == null) {
            lock = manager.createMulticastLock("localWifi");
            lock.setReferenceCounted(true);
        }

        hekrConfig = new HekrConfig();
        uiDscDevUtil = new UIDscDevUtil();

        discoverCallBack();
    }

    private void discoverCallBack() {
        uiDscDevUtil.setListener(new UIDscDevUtil.DeviceListCallBack() {

            @Override
            public void callBackDevice(final FindDeviceBean findDeviceBean) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConfigActivity.this,"局域网发现的设备:"+findDeviceBean.getDevTid(),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void callBackFail() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgressDialog();
                        Toast.makeText(ConfigActivity.this,"配网失败!",Toast.LENGTH_LONG).show();
                    }
                });
                if (lock != null) {
                    lock.release();
                }
            }

            @Override
            public void callBackSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgressDialog();
                        Toast.makeText(ConfigActivity.this,"发现成功!",Toast.LENGTH_LONG).show();
                    }
                });

                if (lock != null) {
                    lock.release();
                }
            }
        });
    }

    private void cancelProgressDialog(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * 监听网络变化
     */
    public void createReceiver() {
        // 创建网络监听广播
        if (connectionReceiver == null) {
            connectionReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                        if (netInfo != null && netInfo.isAvailable()) {
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            String nowWifi = wifiManager.getConnectionInfo().getSSID().replace("\"", "");
                            if (!TextUtils.isEmpty(nowWifi)) {
                                ssid.setText(nowWifi);
                            }
                        } else {
                            ssid.setText("");
                            pwd_input.setText("");
                        }
                    }
                }
            };
            // 注册网络监听广播
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectionReceiver, intentFilter);
        }
    }

    /**
     * 点击配网按钮之后
     * 1、发送ssid &&pwd
     * 2、启动发现服务
     */
    private void config() {
        if (lock != null) {
            lock.acquire();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                hekrConfig.config(ssid.getText().toString(), pwd_input.getText().toString(), 60);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                uiDscDevUtil.startSearch(60);
            }
        }).start();
    }

    /**
     * @return 当前网络是否是wifi
     */
    private boolean netWorkCheck() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable() && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_connect_btn:
                if(!TextUtils.isEmpty(pwd_input.getText().toString().trim())) {
                    if(!isFinishing()) {
                        progressDialog.show();
                        config();
                    }
                }else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(getResources().getString(R.string.app_name));
                    alert.setMessage("密码为空?");
                    alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(netWorkCheck()) {
                                if(!isFinishing()) {
                                    progressDialog.show();
                                    config();
                                }
                            }else{
                                Toast.makeText(ConfigActivity.this,"无可用网络!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    alert.setNegativeButton("取消", null).create().show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
    }
}
