package com.hekr.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.hekr.android.app.model.Global;
import com.hekr.android.app.model.RouterBack;
import com.hekr.android.app.model.SetKeyBack;
import com.hekr.android.app.ui.CustomProgress;
import com.hekr.android.app.util.HttpHelper;
import com.hekr.android.app.util.ThreadPool;
import com.hekr.android.app.util.WifiAdmin;

import com.umeng.analytics.MobclickAgent;

import org.apache.http.params.HttpParams;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AddDeviceActivity extends Activity
{

    private WifiManager wifiManager;//管理wifi
    private ConnectivityManager connectManager;//管理网络
    private WifiInfo myWifiInfo;

    private Spinner spinner;
    private ArrayAdapter<WIFI> adapter;//下拉框适配器
    private String name;
    private SpinnerAdapter spinnerAdapter;

    private final static String TAG="MyLog";
    private CustomProgress addProgressBar;
    private BroadcastReceiver addconnectionReceiver;//监听当前网络状态
    //密码框
    private EditText passwordText;
    private TextView messageText;
    //设置热点，设置成功，按钮
    private ImageButton setVote;
    private ImageButton setOk;
    private ImageView voteMessage;
    private ImageView passwordMessage;
    private ImageView successMessage;
    private String password;
    private CheckBox router_password;
    private Runnable runnable3;
    private Toast toast = null;


    private HttpParams httpParameters;
    private int timeoutConnection = 15000;
    private int timeoutSocket = 15000;

    //扫描出的热点的信息封装（ssid,channel,bssid,encryption）
    private List<WIFI> ssid_list = new ArrayList<WIFI>();
    private ArrayList<ScanResult> list;
    public static class WIFI
    {
        private String ssid;
        private int channel;
        private String bssid;
        private int encryption;

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = WifiAdmin.clearSSID(ssid);
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public String getBssid() {
            return bssid;
        }

        public void setBssid(String bssid) {
            this.bssid = bssid;
        }

        public int getEncryption() {
            return encryption;
        }

        public void setEncryption(int encryption) {
            this.encryption = encryption;
        }

        public String toString()
        {
            return ssid;
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        Log.i("LifeCycle", "AddDeviceActivity--onCreate()被触发");
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);        //获得系统wifi服务
        connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        createReceiver();
                 //存放周围wifi热点对象的列表
        list = (ArrayList<ScanResult>) wifiManager.getScanResults();
        try {
            wifiManager.setWifiEnabled(true);
        }catch (Exception e){
            Log.i("MyLog","不允许打开wifi");
        }
        //List<WIFI> ssid_list = new ArrayList<WIFI>();//存放了热点名称的list
        WIFI firstw = new WIFI();
        firstw.setSsid(getResources().getString(R.string.choose_router).toString());
        ssid_list.add(firstw);
        if(list!=null)
        {
            for (int i = 0; i < list.size(); i++) {
                WIFI w = new WIFI();
                w.setBssid(list.get(i).BSSID);
                w.setChannel(WifiAdmin.getChannelByFrequency(list.get(i).frequency));
                w.setSsid(list.get(i).SSID);
                w.setEncryption(WifiAdmin.getEnncryption(list.get(i).capabilities));

                ssid_list.add(w);
//          Log.v(TAG,"他的ssid:是---"+list.get(i).SSID+"-----加密"+list.get(i).capabilities+"\n");
            }
        }
        spinner = (Spinner) findViewById(R.id.setspinner);//wifi热点选择下拉框
        //下面R.layout.spinner_item里面可以设置spinner初始选中的字的颜色
        adapter = new ArrayAdapter<WIFI>(this,R.layout.spinner_item,ssid_list);
        //下面R.layout.myspinner可以修改选出的wifi的高度之类的
        adapter.setDropDownViewResource(R.layout.myspinner);
        spinner.setPrompt(getResources().getString(R.string.please_choose_wifi).toString());
        //spinnerAdapter = new SpinnerAdapter(AddDeviceActivity.this);
        spinner.setAdapter(adapter);

        spinner.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                ssid_list.clear();
                list = (ArrayList<ScanResult>) wifiManager.getScanResults();
                if(list!=null){
                    Log.i("AddLog","list触发："+list);
                }
//              if(list!=null&&list.size() == 0) {
//                   WIFI w1 = new WIFI();
//                   w1.setSsid(getResources().getString(R.string.cannot_find_wifivote).toString());
//                   ssid_list.add(w1);
//              }
                WIFI firstw = new WIFI();
                firstw.setSsid(getResources().getString(R.string.choose_router).toString());
                ssid_list.add(firstw);
                if(list!=null) {
                    for (int i = 0; i < list.size(); i++) {
                        WIFI w = new WIFI();
                        w.setBssid(list.get(i).BSSID);
                        w.setChannel(WifiAdmin.getChannelByFrequency(list.get(i).frequency));
                        w.setSsid(list.get(i).SSID);
                        w.setEncryption(WifiAdmin.getEnncryption(list.get(i).capabilities));
                        ssid_list.add(w);
                        //Log.v(TAG,"他的ssid:是---"+list.get(i).SSID+"-----加密"+list.get(i).capabilities+"\n");
                    }
                }
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }
            }
        });
        passwordText = (EditText) findViewById(R.id.setmima);
        setOk= (ImageButton) findViewById(R.id.wifiset);
        setVote= (ImageButton)findViewById(R.id.set);
        //setVote.setText("选择设备");
        //setVote.setTextColor(Color.WHITE);
        //setVote.setTextSize(16);

        setVote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                    intent.putExtra("extra_prefs_show_button_bar", true);
                    intent.putExtra("extra_prefs_set_next_text", "完成");
                    intent.putExtra("extra_prefs_set_back_text", "返回");
                    intent.putExtra("wifi_enable_next_on_connect", true);
                    startActivity(intent);
                    toast = Toast.makeText(getApplicationContext(), "请连接以Hekr_开头的热点，默认密码为12345678",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }catch(Exception e){
                    Log.i("MyLog", "跳转到wifi连接页面出错！");
                }
            }
        });

        voteMessage= (ImageView) findViewById(R.id.firsticon);
        passwordMessage= (ImageView) findViewById(R.id.secondicon);
        successMessage= (ImageView) findViewById(R.id.thirdicon);
        messageText= (TextView) findViewById(R.id.votemessage);
        router_password= (CheckBox) findViewById(R.id.router_password);
        router_password.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(router_password.isChecked())
                {
                  /* show the password*/
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                  /* hide the password */
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        runnable3 = new Runnable()
        {
            public void run()
            {
                RouterBack routerBack=new RouterBack();
                SetKeyBack setKeyBack=new SetKeyBack();
                Message msg = new Message();
                Bundle data = new Bundle();
                WIFI w = (WIFI)spinner.getSelectedItem();
                Log.i("MyLog","设置ak向服务器发送的key:"+Global.ACCESSKEY);
                String setKeyBackAccesskey=HttpHelper.doGet(Global.ACCESSKEY, 0);
                Log.i("MyLog","设置ak返回的字符串："+setKeyBackAccesskey);
                data.putInt("code", 1);
                if(w.getBssid()!=null){
                    if(setKeyBackAccesskey!=null)
                    {
                        try {
                            JSONObject keySetOkJson = new JSONObject(setKeyBackAccesskey);
                            setKeyBack.setCode(keySetOkJson.getInt("code"));
                            setKeyBack.setMsg(keySetOkJson.getString("msg"));
                        } catch (JSONException e) {
                            setKeyBack.setCode(1);
                            setKeyBack.setMsg("ak返回json转本地属性异常");
                        }
//                        Log.i("MyLog","backkey:"+setKeyBack.getCode());
//                        Log.i("MyLog","m:"+setKeyBack.getMsg());
                        if(setKeyBack.getCode()==0)
                        {
                            data.putString("setKeyBack",setKeyBackAccesskey);
                            Log.i("MyLog","向服务器发送连接路由器的值："+"名称："+w.ssid+" 密码："+passwordText.getText().toString()+" 信道："+w.getChannel()+" bssid："+w.getBssid()+" 加密方式："+w.getEncryption()+"");
                            String backRouter = getRouterBackString(w.ssid, passwordText.getText().toString(), w.getChannel(), w.getBssid(), w.getEncryption() + "");
                            Log.i("MyLog","backRouter返回的字符串："+backRouter);
                            if(backRouter!=null)
                            {
                                try {
                                    JSONObject routerBackJson = new JSONObject(backRouter);
                                    routerBack.setCode(routerBackJson.getInt("code"));
                                    routerBack.setMsg(routerBackJson.getString("msg"));
                                } catch (JSONException e)
                                {
                                    routerBack.setCode(1);
                                    routerBack.setMsg("路由返回json转本地属性异常");
                                }
                                Log.i("MyLog","routerback:"+routerBack.getCode());
                                Log.i("MyLog","routerm:"+routerBack.getMsg());
                                if(routerBack.getCode()==0)
                                {
                                    data.putInt("code", 0);
                                    data.putString("routerBack", backRouter);
                                }
                                else{
                                    data.putInt("code", 1);
                                    data.putString("result", getResources().getString(R.string.bound_router_failed).toString());
                                }
                            }
                            else{
                                data.putString("result",getResources().getString(R.string.bound_router_network_error).toString());
                            }
                        }
                        else{
                            data.putString("result",getResources().getString(R.string.bound_user_message_error).toString());
                        }
                    }
                    else{
                        data.putString("result",getResources().getString(R.string.bound_user_message_network_error).toString());
                    }
                }else{
                    data.putString("result",getResources().getString(R.string.wifi_vote_error_network_error).toString());
                }
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };

        setOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nowWifi= WifiAdmin.clearSSID(wifiManager.getConnectionInfo().getSSID());
                Log.v("MyLog","----------"+nowWifi);
                if(Global.ACCESSKEY==null) {
                    Toast.makeText(AddDeviceActivity.this, getResources().getString(R.string.please_connect_network_then_adddevice).toString(), Toast.LENGTH_SHORT).show();
                }
                else{
                    if (nowWifi != null && nowWifi.length() > 5 && "HEKR_".equalsIgnoreCase(nowWifi.substring(0, 5)))
                    {
                        name = spinner.getSelectedItem().toString();
                        Log.v(TAG, "用户选择的路由wifi名称：" + name+" wifi密码："+ passwordText.getText().toString());
                        password=passwordText.getText().toString();
                        if (name == null||passwordText.getText().toString().trim()==null||"".equals(passwordText.getText().toString().trim())) {
                            Toast.makeText(AddDeviceActivity.this, getResources().getString(R.string.please_choose_message).toString(), Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddDeviceActivity.this);
                            builder.setMessage(getResources().getString(R.string.AlertDialog_message).toString());
                            builder.setTitle(getResources().getString(R.string.AlertDialog_title).toString());
                            builder.setPositiveButton(getResources().getString(R.string.AlertDialog_positiveButton).toString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(dialog!=null){
                                        dialog.dismiss();
                                    }
                                    passwordMessage.setBackgroundResource(R.drawable.dagou);
                                    addProgressBar = CustomProgress.show(AddDeviceActivity.this, getResources().getString(R.string.adding_device).toString(), false, null);
                                    ThreadPool threadPool = ThreadPool.getThreadPool();
                                    threadPool.addTask(runnable3);
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.AlertDialog_negativeButton).toString(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(dialog!=null){
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder.create().show();
                        }else{
                            passwordMessage.setBackgroundResource(R.drawable.dagou);
                            addProgressBar = CustomProgress.show(AddDeviceActivity.this, getResources().getString(R.string.adding_device).toString(), false, null);
                            ThreadPool threadPool = ThreadPool.getThreadPool();
                            threadPool.addTask(runnable3);
                        }
                    } else {
                        Toast.makeText(AddDeviceActivity.this, getResources().getString(R.string.please_connect_device_vote).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            Log.i("MyLog","线程已经向handler中传code，值为:"+data.getInt("code"));
            if(data.getInt("code")==0)
            {
                if(addProgressBar!=null){
                    addProgressBar.dismiss();
                }
                WIFI w = (WIFI)spinner.getSelectedItem();
                Log.i(TAG,"选择的wifi名称："+w.getSsid()+"用户输入的密码："+passwordText.getText().toString()+"用户选择的wifi加密方式："+w.getEncryption());
                WifiConfiguration config3=WifiAdmin.CreateWifiInfo(w.getSsid(), passwordText.getText().toString() , w.getEncryption());
                int netID2 = wifiManager.addNetwork(config3);
                boolean bRet2 = wifiManager.enableNetwork(netID2, true);
                wifiManager.updateNetwork(config3);
                successMessage.setBackgroundResource(R.drawable.dagou);
                Intent it = new Intent();
                it.setClass(AddDeviceActivity.this, MainActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
                //finish();
            }
            else{
                if(addProgressBar!=null){
                    addProgressBar.dismiss();
                }
                Toast.makeText(AddDeviceActivity.this,data.getString("result"),Toast.LENGTH_SHORT).show();
            }
        }
    };

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
                            if(nowWifi!=null&&nowWifi.length()>5&&"HEKR_".equalsIgnoreCase(nowWifi.substring(0,5)))
                            {
                                messageText.setText(getResources().getString(R.string.now_connectting_vote).toString()+nowWifi);
                                voteMessage.setBackgroundResource(R.drawable.dagou);
                                setVote.setBackgroundResource(R.drawable.selecteddevicezh);
                                WIFI w = (WIFI)spinner.getSelectedItem();

                            }
                            else{
                                voteMessage.setBackgroundResource(R.drawable.yihao);
                                setVote.setBackgroundResource(R.drawable.choosevotedevice);
                                messageText.setText("");
                            }

                        }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET)
                        {

                        }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){

                        }
                    }
                    else {
                        //网络断开
                        voteMessage.setBackgroundResource(R.drawable.yihao);
                        setVote.setBackgroundResource(R.drawable.choosevotedevice);
                        messageText.setText("");
                    }
                }
            }
        };
        // 注册网络监听广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(addconnectionReceiver, intentFilter);
    }

    protected void onResume()
    { //变成竖屏
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        super.onResume();
        Log.i("LifeCycle", "AddDeviceActivity--onResume()被触发");
        MobclickAgent.onResume(this);
    }

    protected void onDestroy() {
        Log.i("LifeCycle", "AddDeviceActivity--onDestroy()被触发");
        if(addconnectionReceiver!=null){
            unregisterReceiver(addconnectionReceiver);
        }
        super.onDestroy();
    }

    //让热点设备连接路由器
    public String getRouterBackString(String name,String password,int channel,String bssid,String encryption)
    {
        try {
            bssid = URLEncoder.encode(bssid, "utf-8");
        } catch (UnsupportedEncodingException e) {
            bssid=null;
            Log.i("MyLog","bssid解码成utf-8出错:"+e.getMessage());
        }
        try {
            password = URLEncoder.encode(password,"utf-8");
        } catch (UnsupportedEncodingException e) {
            password=null;
            Log.i("MyLog","用户添加设备时输入的password解码成utf-8出错:"+e.getMessage());
        }
        try {
            name = URLEncoder.encode(name,"utf-8");
        } catch (UnsupportedEncodingException e) {
            name=null;
            Log.i("MyLog","用户选择的路由器name解码成utf-8出错:"+e.getMessage());
        }
        String str="http://192.168.10.1/t/set_bridge?ssid="+name+"&channel="+channel+"&bssid="+bssid+"&encryption="+encryption+"&key="+password;
        URL url= null;
        HttpURLConnection connection=null;
        try {
            url = new URL(str);
            connection=(HttpURLConnection) url.openConnection();
            //设置连接主机超时（单位：毫秒）
            connection.setConnectTimeout(5*1000);
            //设置从主机读取数据超时（单位：毫秒）
            connection.setReadTimeout(5*1000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                String backurl = "";
                while ((inputLine = in.readLine()) != null)
                {
                    backurl += inputLine+ "\n";
                }
                return backurl;
            }
            else{
                Log.d("MyLog", "服务器取routerBack不等于200");
                return null;
            }
        } catch (Exception e) {
            Log.d("MyLog","设置路由器报异常:"+e.getMessage(),e);
            connection.disconnect();
            if(e instanceof java.net.SocketException)
            {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e1) {
                    Log.d("MyLog", "休眠出错");
                }
                runnable3.run();
            }
            return null;
        }

    }
    public void onPause() {
        super.onPause();
        Log.i("LifeCycle", "AddDeviceActivity--onPause()被触发");
        MobclickAgent.onPause(this);
    }
}