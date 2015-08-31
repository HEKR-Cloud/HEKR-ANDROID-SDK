package com.hekr.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import com.hekr.android.app.model.Global;
import com.hekr.android.app.util.AssetsDatabaseManager;
import com.hekr.android.app.util.HttpHelper;
import com.hekr.android.app.util.UpdateManager;
import android.util.Log;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.igexin.sdk.PushManager;

import java.util.Iterator;

/**
 * Created by xubukan on 2015/3/18.
 */
public class LoginActivity extends Activity
{
    private String os;
    private String osversion;
    private String type;
    private String appversion;
    private long firstime = 0;
    private float ratio;
    public static Context globalContext;
    Handler loginHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(Global.apkUrl !=null)
            {
                String apkurl =  Global.apkUrl.trim();
                if(!"".equals(apkurl) && apkurl.endsWith(".apk")){
                    UpdateManager mUpdateManager = new UpdateManager(LoginActivity.this);
                    mUpdateManager.checkUpdateInfo();
                }
            }
        }
    };
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        globalContext=this;
        MobclickAgent.updateOnlineConfig( LoginActivity.this );
        AnalyticsConfig.enableEncrypt(true);


        PushManager.getInstance().initialize(this.getApplicationContext());

        AssetsDatabaseManager.initManager(getApplication());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db = mg.getDatabase("db");

        //更新提供的字段：什么系统、设备类型、手机版本、appversion
        os="android";
        osversion=android.os.Build.VERSION.RELEASE.charAt(0)+"";
        type="phone";
        appversion=getVerName(LoginActivity.this);

        Log.d("MyLog", os+"---------"+osversion+"---------"+type+"---------"+getVerName(LoginActivity.this));
        final String updateUrl="http://update.hekr.me/appupdate?os="+os+"&osversion="+osversion+"&appversion="+appversion+"&type="+type;
        Runnable updateRunnable=new Runnable()
        {
            @Override
            public void run() {
                Message msg = new Message();
                Global.apkUrl=HttpHelper.getApkUrl(updateUrl);
                Log.d(LoginActivity.class.getSimpleName(),"从服务器获取的更新url："+Global.apkUrl);
                loginHandler.sendMessage(msg);
            }
        };

        Cursor cursor = null;
        try {
            //从数据库查询是否存在key
            cursor = db.rawQuery("select setting_value from settings where setting_key=?",
                    new String[]{"user_credential"});

            if (cursor.moveToNext())
            {
                String user_credential = cursor.getString(0);
                Intent it = new Intent();
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.setClass(LoginActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
            else{
                new Thread(updateRunnable).start();
            }
        }catch (Exception keye){
            Log.d(LoginActivity.class.getSimpleName(),"Login从数据库查询key异常："+keye.getMessage());
        }finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo("com.hekr.android.app", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }
    public String getVersion() {
        try {
            return this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA).metaData.getString("version").toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
    public String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
            if (TextUtils.isEmpty(verName))
            {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    //直接在xml中触发调用
    public void loginQQ(View view){
        startLoginWeb("http://login.hekr.me/oauth.htm?type=qq");
    }

    public void loginTwitter(View view){
        startLoginWeb("http://login.smartmatrix.mx/oauth.htm?type=tw");
    }

    public void loginWeibo(View view){
        startLoginWeb("http://login.hekr.me/oauth.htm?type=weibo");
    }

    public void loginGoogle(View view){
        startLoginWeb("http://login.smartmatrix.mx/oauth.htm?type=g");
    }

    private void startLoginWeb(String url)
    {
        Intent  it = new Intent();
        it.putExtra("url",url);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        it.setClass(LoginActivity.this, LoginWebActivity.class);
        startActivity(it);
        finish();
    }
    protected void onResume()
    { /** * 设置为竖屏 */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondtime = System.currentTimeMillis();
            if (secondtime - firstime > 3000) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.press_again_exit).toString(), Toast.LENGTH_SHORT).show();
                firstime = System.currentTimeMillis();
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
