package com.hekr.android.app;

import android.app.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.*;
import com.hekr.android.app.model.Global;
import com.hekr.android.app.model.KeyBack;
import com.hekr.android.app.ui.SliderMenu;
import com.hekr.android.app.util.*;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class MainActivity extends ActivityGroup {
    private final static String TAG = "MyLog";
    private SliderMenu mMenu;//左右滑动抽屉式菜单，继承了HorizontalScrollView的一个实现类
    private LinearLayout mContainer;
    private static Context context;
    private WifiManager wifiManager;//管理wifi
    private BroadcastReceiver connectionReceiver;//广播
    private MyApplication gloabData;

    private long firstime = 0;

    //处理获取设备用户key的线程
    static Handler mainHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            KeyBack key = new KeyBack();
            String deviceAccesskey_str = data.getString("deviceAccesskey");
            String userAccessKey_str = data.getString("userAccessKey");
            if (deviceAccesskey_str != null) {
                try {
                    JSONObject deviceAccesskeyJson = new JSONObject(deviceAccesskey_str);
                    key.setUid(deviceAccesskeyJson.getString("uid"));
                    key.setTime(deviceAccesskeyJson.getLong("time"));
                    key.setType(deviceAccesskeyJson.getString("type"));
                    key.setToken(deviceAccesskeyJson.getString("token"));
                    Global.ACCESSKEY = deviceAccesskeyJson.getString("token");
                    //Log.i(TAG,"mainHandler_DEVICEACCESSKEY:"+Global.ACCESSKEY);
                } catch (JSONException e) {
                    Log.i(TAG, "mainHandler中设置deviceAccesskey出现异常：" + e.getMessage());
                }
            }
            if (userAccessKey_str != null) {
                try {
                    JSONObject UserAccesskeyJson = new JSONObject(userAccessKey_str);
                    Global.USERACCESSKEY = UserAccesskeyJson.getString("token");
                    //Log.i("MyLog","mainHandler_USERACCESSKEY:"+Global.USERACCESSKEY);
                } catch (JSONException e) {
                    Log.i(TAG, "mainHandler中设置userAccesskey出现异常：" + e.getMessage());
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //用于友盟用户使用数据统计
        MobclickAgent.updateOnlineConfig(MainActivity.this);
        AnalyticsConfig.enableEncrypt(true);
        Log.i("LifeCycle", "MainActivity--onCreate()被触发");
        AssetsDatabaseManager.initManager(getApplication());
        context = this;
        gloabData= (MyApplication) getApplication();

        //获取两个key
        if (Global.ACCESSKEY == null || Global.USERACCESSKEY == null) {
            ThreadPool threadPool = ThreadPool.getThreadPool();
            threadPool.addTask(MainActivity.keyRunnable);
        }
        //检测到服务器的资源刷新处理
        CacheHelper.doUpdateProductIconList();
        CacheHelper.doUpdateProductHtmlList(this);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        mMenu = (SliderMenu) findViewById(R.id.id_menu);
        mContainer = (LinearLayout) findViewById(R.id.id_container);
        //在Mainactivity中添加一个View(ListDeviceActivity)
        mContainer.addView(getLocalActivityManager().startActivity("device", new Intent(MainActivity.this, ListDeviceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView());
        //创建广播监听网络状况
        createReceiver();

        //关于我们
        TextView aboutOurs = (TextView) findViewById(R.id.aboutOurs);
        //帮助（吐槽）
        TextView useHelp = (TextView) findViewById(R.id.useHelp);
        //登出
        TextView logout = (TextView) findViewById(R.id.logout);

        //关于我们
        aboutOurs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, AboutOursActivity.class);
                startActivity(i);
            }
        });
        //使用帮助(吐槽)
        useHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, TuCaoActivity.class);
                startActivity(i);
            }
        });
        //登出
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCookies(MainActivity.this);
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                SQLiteDatabase db = mg.getDatabase("db");
                Log.i(TAG, "执行退出");
                db.execSQL("delete from settings");
                MobclickAgent.onKillProcess(MainActivity.this);
                Intent it = new Intent();
                it.setClass(MainActivity.this, LoginActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
                finish();
            }
        });
    }

    private void createReceiver() {
        // 创建网络监听广播
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                    ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                    if (netInfo != null && netInfo.isAvailable()) {
                        //网络连接
                        String name = netInfo.getTypeName();
                        ThreadPool threadPool = ThreadPool.getThreadPool();
                        threadPool.addTask(ListDeviceActivity.lRunnable);
                        if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            //WiFi网络
                            Toast.makeText(context, getResources().getString(R.string.checked_have_wifi).toString(), Toast.LENGTH_SHORT).show();
                            Global.isWIFI=true;
                            gloabData.setWifiStatus(true);
                            Log.i("MyLog","当前网络是否是wifi连接："+gloabData.isWifiStatus());

                        } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                            Global.isWIFI=false;
                            gloabData.setWifiStatus(false);
                            Log.i("MyLog","当前网络是否是wifi连接："+gloabData.isWifiStatus());
                            //有线网络
                            //Toast.makeText(context,getResources().getString(R.string.checked_have_network).toString(),Toast.LENGTH_SHORT).show();

                        } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            //3g网络
                            Global.isWIFI=false;
                            gloabData.setWifiStatus(false);
                            Log.i("MyLog","当前网络是否是wifi连接："+gloabData.isWifiStatus());
                            Toast.makeText(context, getResources().getString(R.string.network_three_g).toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //网络断开
                        Global.isWIFI=false;
                        gloabData.setWifiStatus(false);
                        Log.i("MyLog","当前网络是否是wifi连接："+gloabData.isWifiStatus());
                        Toast.makeText(context, getResources().getString(R.string.checked_network_disconnect).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        // 注册网络监听广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);

    }

    //切换菜单
    public void toggleMenu(View view) {
        mMenu.toggle();
    }

    //进入一键配置添加模块
    public void addDevice(View view) {
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        try {
            wifiManager.setWifiEnabled(true);
        } catch (Exception e) {
            Log.i("MyLog", "不允许打开wifi");
        }

        if (Global.ACCESSKEY == null) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.get_user_message).toString(), Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(MainActivity.this, HekrConfigActivity.class);
            Log.i("GetuiSdkDemo", "MainActivity:i:---" + i);
            startActivity(i);
        }

    }

    //清除cookie
    private void clearCookies(Context context) {
        CookieSyncManager cookieSyncMngr =
                CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        Log.i("MyLog", "退出软件，清除cookie");
    }

    //销毁广播
    protected void onDestroy() {
        Log.i("LifeCycle", "MainActivity--onDestroy()被触发");
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
        super.onDestroy();
    }

    //竖屏
    protected void onResume() { /** * 设置为竖屏 */
        Log.i("LifeCycle", "MainActivity--onResume()被触发");
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        Log.i("LifeCycle", "MainActivity--onPause()被触发");
        MobclickAgent.onPause(this);
    }

    static Runnable keyRunnable = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            String deviceAccesskey = HttpHelper.doGet("http://user.hekr.me/token/generate.json?type=DEVICE&");
            String userAccessKey = HttpHelper.doGet("http://user.hekr.me/token/generate.json?type=USER&");
            if (deviceAccesskey != null && userAccessKey != null) {
                data.putString("deviceAccesskey", deviceAccesskey);
                data.putString("userAccessKey", userAccessKey);
                Log.i(TAG, "KeyRunnable获取的deviceAccesskey:" + data.getString("deviceAccesskey"));
                Log.i(TAG, "KeyRunnable获取的userAccessKey:" + data.getString("userAccessKey"));
                msg.setData(data);
                mainHandler.sendMessage(msg);
            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.i("MyLog", "获取两个key休眠出错");
                }
                //如果获取失败，休眠3.0秒再次查询key
                ThreadPool threadPool = ThreadPool.getThreadPool();
                threadPool.addTask(this);
            }
        }
    };

    public static boolean isWIFINet(Context context)
    {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {

            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;

    }
}
