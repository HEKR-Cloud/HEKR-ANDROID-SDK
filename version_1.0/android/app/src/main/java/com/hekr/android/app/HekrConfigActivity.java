package com.hekr.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Looper;

import java.net.DatagramPacket;

import com.hekr.android.app.model.Global;
import com.hekr.android.app.ui.CustomProgress;
import com.hekr.android.app.util.HekrConfig;
import com.hekr.android.app.util.ThreadPool;
import com.hekr.android.app.util.UDPConfig;
import com.hekr.android.app.util.WifiAdmin;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

/**
 * Created by kj on 15/6/18.
 */
public class HekrConfigActivity extends Activity {
    private BroadcastReceiver addconnectionReceiver;//监听当前网络状态

    private WifiManager wifiManager;//管理wifi
    static private EditText passwordText;
    static private EditText ssidText;
    private ImageButton doneButton;
    private TextView softAPButton;
    private static boolean isConfigEnd=false;
    private static CustomProgress configProgressBar;
    private static WifiManager.MulticastLock lock=null;
    private static String countryCategory;
    private static Context alertContext;

    public static class  DoneClickListener implements View.OnClickListener{
        private static Activity activity;
        public DoneClickListener(Activity activity){
            this.activity = activity;
        }
        static HekrConfig hc = null;

        @Override
        public void onClick(View v) {
            if("".equals(ssidText.getText().toString().trim())||ssidText.getText().toString().trim()==null){
                if (!"".equals(countryCategory) && countryCategory.equals("CN")) {
                    Toast.makeText(activity, "设备未进入配置模式，请检查手机wifi", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(activity, "The device has not entered the configuration mode", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                if ("".equals(passwordText.getText().toString().trim())||passwordText.getText().toString().trim()==null) {
                    if (!"".equals(countryCategory) && countryCategory.equals("CN")) {
                        //Toast.makeText(activity, "密码为空", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(alertContext);
                        builder.setMessage("密码为空，确认继续添加吗?");
                        builder.setTitle("提示");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(dialog!=null){
                                    dialog.dismiss();
                                }
                                if (lock != null) {
                                    lock.acquire();
                                }

                                //isConfigEnd = false;
                                try {
                                    configProgressBar = CustomProgress.show(activity, activity.getResources().getString(R.string.adding_device).toString(), true, new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            //isConfigEnd = true;
                                            if (hc != null) {
                                                hc.stop();
                                            }
                                        }
                                    });
                                } catch (Exception ex) {
                                }

                                new Thread() {
                                    @Override
                                    public void run() {
                                        hc = new HekrConfig(Global.ACCESSKEY);
                                        Object ret = hc.config(ssidText.getText() + "", passwordText.getText() + "");

                                        if (configProgressBar != null) {
                                            configProgressBar.dismiss();
                                        }

                                        if (lock != null) {
                                            lock.release();
                                        }

                                        if (ret != null) {
                                            //成功返回上一级
                                            //Toast.makeText( activity , "SUCCESS", Toast.LENGTH_SHORT ).show();
                                            Intent i = new Intent(activity, MainActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            activity.startActivity(i);
                                        } else {
                                            Looper.prepare();
                                            if (!"".equals(countryCategory) && countryCategory.equals("CN")) {
                                                Toast.makeText(activity, "连接失败", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(activity, "Config Fail", Toast.LENGTH_SHORT).show();
                                            }
                                            Looper.loop();
                                            //失败提示用户可以进入软Ap模式
                                        }

                                    }
                                }.start();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(dialog!=null){
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.create().show();
                    }
                    else{
                        //Toast.makeText(activity, "Empty password", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(alertContext);
                        builder.setMessage("password to be empty, confirm continue to add?");
                        builder.setTitle("Prompt");
                        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(dialog!=null){
                                    dialog.dismiss();
                                }
                                if (lock != null) {
                                    lock.acquire();
                                }

                                //isConfigEnd = false;
                                try {
                                    configProgressBar = CustomProgress.show(activity, activity.getResources().getString(R.string.adding_device).toString(), true, new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            //isConfigEnd = true;
                                            if (hc != null) {
                                                hc.stop();
                                            }
                                        }
                                    });
                                } catch (Exception ex) {
                                }

                                new Thread() {
                                    @Override
                                    public void run() {
                                        hc = new HekrConfig(Global.ACCESSKEY);
                                        Object ret = hc.config(ssidText.getText() + "", passwordText.getText() + "");

                                        if (configProgressBar != null) {
                                            configProgressBar.dismiss();
                                        }

                                        if (lock != null) {
                                            lock.release();
                                        }

                                        if (ret != null) {
                                            //成功返回上一级
                                            //Toast.makeText( activity , "SUCCESS", Toast.LENGTH_SHORT ).show();
                                            Intent i = new Intent(activity, MainActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            activity.startActivity(i);
                                        } else {
                                            Looper.prepare();
                                            if (!"".equals(countryCategory) && countryCategory.equals("CN")) {
                                                Toast.makeText(activity, "连接失败", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(activity, "Config Fail", Toast.LENGTH_SHORT).show();
                                            }
                                            Looper.loop();
                                            //失败提示用户可以进入软Ap模式
                                        }

                                    }
                                }.start();
                            }
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(dialog!=null){
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.create().show();
                    }
                } else {
                    if (lock != null) {
                        lock.acquire();
                    }

                    //isConfigEnd = false;
                    try {
                        configProgressBar = CustomProgress.show(activity, activity.getResources().getString(R.string.adding_device).toString(), true, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                //isConfigEnd = true;
                                if (hc != null) {
                                    hc.stop();
                                }
                            }
                        });
                    } catch (Exception ex) {
                    }

                    new Thread() {
                        @Override
                        public void run() {
                            hc = new HekrConfig(Global.ACCESSKEY);
                            Object ret = hc.config(ssidText.getText() + "", passwordText.getText() + "");

                            if (configProgressBar != null) {
                                configProgressBar.dismiss();
                            }

                            if (lock != null) {
                                lock.release();
                            }

                            if (ret != null) {
                                //成功返回上一级
                                //Toast.makeText( activity , "SUCCESS", Toast.LENGTH_SHORT ).show();
                                Intent i = new Intent(activity, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(i);
                            } else {
                                Looper.prepare();
                                if (!"".equals(countryCategory) && countryCategory.equals("CN")) {
                                    Toast.makeText(activity, "连接失败", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, "Config Fail", Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                                //失败提示用户可以进入软Ap模式
                            }

                        }
                    }.start();
                }
            }
        }
    }


    private void createReceiver()
    {
        // 创建网络监听广播
        addconnectionReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
                {

                    ConnectivityManager mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                    if(netInfo != null && netInfo.isAvailable())
                    {

                        //网络连接
                        if(netInfo.getType()==ConnectivityManager.TYPE_WIFI)
                        {
                            //WiFi网络
                            //Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                            String nowWifi= WifiAdmin.clearSSID(wifiManager.getConnectionInfo().getSSID());
                            if(nowWifi!=null)
                            {
                                ssidText.setText(nowWifi);
                            }
                            else{
                                ssidText.setText("");
                            }

                        }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET)
                        {
                            //有线网络
                            ssidText.setText("");

                        }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                            //3g网络
                            ssidText.setText("");
                        }
                    }
                    else {
                        //网络断开
                        ssidText.setText("");
                    }
                }
            }
        };
        // 注册网络监听广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(addconnectionReceiver, intentFilter);
    }

    public static class  SoftAPClickListener implements   View.OnClickListener{
        private Activity activity;
        public SoftAPClickListener(Activity activity){
            this.activity = activity;
        }
        @Override
        public void onClick(View v) {
            Intent i = new Intent(activity, AddDeviceActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(i);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        alertContext=this;
        WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(lock==null) {
            lock = manager.createMulticastLock("localWifi");
            lock.setReferenceCounted(true);
        }
        createReceiver();
        setContentView(R.layout.hekr_config);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //init comp
        ssidText = (EditText)findViewById(R.id.ssid);
        passwordText = (EditText)findViewById(R.id.setmima);
        doneButton = (ImageButton)findViewById(R.id.wifiset);
        softAPButton = (TextView)findViewById(R.id.softap);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);//获得系统wifi服务
        ConnectivityManager connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        WifiAdmin wifiAdmin = new WifiAdmin(this);
        String ssid = wifiAdmin.getSSID()==null? "":wifiAdmin.getSSID();

        ssidText.setText(ssid);
        ssidText.setEnabled(false);

        //set event
        doneButton.setOnClickListener(new DoneClickListener(this));
        softAPButton.setOnClickListener(new SoftAPClickListener(HekrConfigActivity.this));

        countryCategory=getResources().getConfiguration().locale.getCountry();
    }
    //变成竖屏
    protected void onResume()
    { /** * 设置为横屏 */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onDestroy() {
        if(addconnectionReceiver!=null){
            unregisterReceiver(addconnectionReceiver);
        }
        super.onDestroy();
        Log.i("LifeCycle","HekrConfigActivity--onDestroy()被触发");
    }

}