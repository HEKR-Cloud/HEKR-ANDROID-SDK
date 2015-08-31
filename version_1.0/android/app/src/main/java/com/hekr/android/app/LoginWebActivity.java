package com.hekr.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;
import com.hekr.android.app.Interface.ConfigManage;
import com.hekr.android.app.ui.CustomProgress;
import com.hekr.android.app.util.AssetsDatabaseManager;
import android.util.Log;
import com.hekr.android.app.util.ThreadPool;
import com.umeng.analytics.MobclickAgent;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import java.util.HashMap;

/**
 * Created by xubukan on 2015/3/20.
 */
public class LoginWebActivity extends Activity implements ConfigManage {

    private WebView mWeb;
    private CustomProgress loginWebProgressBar;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_web);

        //清除Cookie
        clearCookies(this);
        //login跳入，并传递了url
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        mWeb = (WebView)findViewById(R.id.id_webview);
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        loginWebProgressBar = CustomProgress.show(LoginWebActivity.this, getResources().getString(R.string.login_loadding).toString(), false, null);

        mWeb.setWebViewClient(new WebViewClient()
        {
            //对网页中超链接按钮的响应。当按下某个连接时WebViewClient会调用这个方法，并传递参数
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if(url.contains("success.htm"))
                {
                    mWeb.setVisibility(View.INVISIBLE);
                }
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if (loginWebProgressBar.isShowing()) {
                    loginWebProgressBar.dismiss();
                }

                if(url.contains("success.htm"))
                {
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookiestr = cookieManager.getCookie(url);
                    //例如 cookiestr:u=k018RfmBjlml%2BhOEIROeJheEuuxNYI6Pigl%2FhTZbqrYU3T2qUp2pB1IQ%3D%3D;_csrftoken_=ClNi
                    //Log.i(LoginWebActivity.class.getSimpleName(),"LoginWeb中cookiestr:-----"+cookiestr);
                    HashMap<String,String> cookieMap = new HashMap<String, String>();
                    if(cookiestr!=null)
                    {
                        String cookieParams[] = cookiestr.split(";");
                        //切割成了u=k018RfmBjlml%2BhOEIROeJheEuuxNYI6Pigl%2FhTZbqrYU3T2qUp2pB1IQ%3D%3D
                        //       _csrftoken_=ClNi
                        //Log.i("CookieLog","cookiestr为："+cookiestr+"------"+cookieParams[0]+":"+cookieParams[1]);

                        if(cookieParams.length>0)
                        {
                            for(int i=0;i<cookieParams.length;i++)
                            {
                                String kvParam[] = cookieParams[i].split("=");
                                //切割成u
                                //k018RfmBjlml%2BhOEIROeJheEuuxNYI6Pigl%2FhTZbqrYU3T2qUp2pB1IQ%3D%3D
                                //_csrftoken_
                                //ClNi
                                if(kvParam.length==2)
                                {
                                    cookieMap.put(kvParam[0],kvParam[1]);
                                }
                            }
                        }
                        if(cookieMap.containsKey("u"))
                        {
                            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                            SQLiteDatabase db = mg.getDatabase("db");
                            db.execSQL("INSERT INTO settings VALUES('user_credential','"+cookieMap.get("u")+"');");

                            //Log.i(LoginWebActivity.class.getSimpleName(),"从url切割出来的logincookie值:"+cookieMap.get("u"));
                            ThreadPool threadPool = ThreadPool.getThreadPool();
                            threadPool.addTask(MainActivity.keyRunnable);
                            Intent  it = new Intent();
                            it.setClass(LoginWebActivity.this, MainActivity.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(it);
                            finish();
                            return;
                        }
                    }
                }

                super.onPageFinished(view, url);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {

            }
        });

        mWeb.loadUrl(url);
    }

    private void clearCookies(Context context)
    {
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }
    public void navBack(View view)
    {
        Intent  it = new Intent();
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        it.setClass(LoginWebActivity.this, LoginActivity.class);
        startActivity(it);
        finish();
    }
    //竖屏
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
            Intent  it = new Intent();
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.setClass(LoginWebActivity.this, LoginActivity.class);
            startActivity(it);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public String lastTemplatesHash() {
        return null;
    }

    @Override
    public void saveLastTemplatesHash() {

    }

    @Override
    public HashMap<String, String> lastTokens() {
        return null;
    }

    @Override
    public void saveLastTokens(HashMap<String, String> hashMap) {
        if(hashMap.containsKey("u")) {
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
            SQLiteDatabase db = mg.getDatabase("db");
            db.execSQL("INSERT INTO settings VALUES('user_credential','" + hashMap.get("u") + "');");
        }
    }
}
