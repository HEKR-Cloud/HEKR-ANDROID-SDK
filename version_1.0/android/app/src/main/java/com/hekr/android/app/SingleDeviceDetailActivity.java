package com.hekr.android.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import com.hekr.android.app.Interface.ResourceManage;
import com.hekr.android.app.model.Global;
import com.hekr.android.app.ui.CustomProgress;
import com.hekr.android.app.util.AssetsDatabaseManager;
import com.lambdatm.runtime.lang.Cell;
import com.lambdatm.runtime.lib.Base;
import com.lambdatm.runtime.util.Util;
import com.umeng.analytics.MobclickAgent;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xubukan on 2015/3/27.
 */
public class SingleDeviceDetailActivity extends Activity {
    static  XWalkView mWeb;
    private ProgressDialog vProgressBar;

    private String pid;//厂家id
    private String cid;//设备种类id
    private String mid;
    private String tid;
    private CustomProgress detailProgressBar;



    class ResourceClient extends XWalkResourceClient {

        public ResourceClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
            Log.d("MyLog", "Load Started:" + url);
        }

        public void onLoadFinished(XWalkView view, String url) {
            //super.onLoadFinished(view, url);
            Log.d("MyLog", "Load Finished:" + url);
            if (detailProgressBar!=null&&detailProgressBar.isShowing()) {
                detailProgressBar.dismiss();
            }
            super.onLoadFinished(view, url);
        }

        public void onProgressChanged(XWalkView view, int progressInPercent) {
            super.onProgressChanged(view, progressInPercent);

            Log.d("MyLog", "Loading Progress:" + progressInPercent);
        }

        public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
            Log.d("MyLog", "Intercept load request");
            return super.shouldInterceptLoadRequest(view, url);
        }

        public void onReceivedLoadError(XWalkView view, int errorCode, String description,
                                        String failingUrl) {
            Log.d("MyLog", "Load Failed:" + description);
            if (detailProgressBar!=null&&detailProgressBar.isShowing()) {
                detailProgressBar.dismiss();
            }
            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }
    }
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_device_detail);
        mWeb = (XWalkView)findViewById(R.id.id_device_web);

        //传过来的设备信息
        Intent i=getIntent();
        mid="1";

        String lang=getResources().getConfiguration().locale.getLanguage()+"-"+ getResources().getConfiguration().locale.getCountry();
        String user=((TelephonyManager)SingleDeviceDetailActivity.this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();

        detailProgressBar = CustomProgress.show(SingleDeviceDetailActivity.this, getResources().getString(R.string.login_loadding).toString(), true, null);
        mWeb.setResourceClient(new ResourceClient(mWeb));
//        if(mid!=null&&tid!=null&&Global.USERACCESSKEY!=null&&user!=null){
//            mWeb.load("http://app.hekr.me/android/" + mid + "/index.html?access_key=" + Global.USERACCESSKEY + "&tid=" + tid + "&lang=" + lang+"&user="+user,null);
//            //XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//        }
        mWeb.load("http://www.baidu.com",null);
        Log.i("MyLog","detail-url:"+"http://app.hekr.me/android/" + mid + "/index.html?access_key=" + Global.USERACCESSKEY + "&tid=" + tid + "&lang=" + lang+"&user="+user);

    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    protected void onDestroy() {
        super.onDestroy();
        if (mWeb != null) {
            mWeb.onDestroy();
        }
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

    public void navBack(View view)
    {
        this.finish();
    }
}
