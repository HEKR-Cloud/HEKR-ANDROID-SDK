package com.hekr.android.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.*;
import com.hekr.android.app.Interface.ResourceManage;
import com.hekr.android.app.model.Global;
import com.hekr.android.app.ui.CustomProgress;
import android.util.Log;
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
public class DeviceDetailActivity extends Activity implements ResourceManage{
    static  XWalkView mWeb;
    private ProgressDialog vProgressBar;

    private Handler handler=new Handler();
    private String pid;//厂家id
    private String cid;//设备种类id
    private String mid;
    private String tid;
    private String name;
    private CustomProgress detailProgressBar;


    @Override
    public String templatePath(String templateId) {
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db = mg.getDatabase("db");
        if(templateId!=null){
            Cursor cursor=null;
            try {
                cursor = db.rawQuery("select path from page where id=?",
                        new String[]{templateId});
                cursor.moveToNext();
                return cursor.getString(0);
            }
            catch(Exception e){
                return null;
            }finally {
                if(cursor!=null){
                    cursor.close();
                }
            }
        }else{
            return null;
        }

    }

    @Override
    public boolean saveTemplate(String templateId, String path) {
        return false;
    }

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
        setContentView(R.layout.activity_device_detail);
        mWeb = (XWalkView)findViewById(R.id.id_device_web);


        //传过来的设备信息
        Intent i=getIntent();
        pid=getPid(i.getStringExtra("detail"));
        cid=getCid(i.getStringExtra("detail"));
        mid=getMid(i.getStringExtra("detail"));
        tid=i.getStringExtra("tid");

        String lang=getResources().getConfiguration().locale.getLanguage()+"-"+ getResources().getConfiguration().locale.getCountry();
        String user=((TelephonyManager)DeviceDetailActivity.this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();

        detailProgressBar = CustomProgress.show(DeviceDetailActivity.this, getResources().getString(R.string.login_loadding).toString(), true, null);
        mWeb.setResourceClient(new ResourceClient(mWeb));
        if(mid!=null&&tid!=null&&Global.USERACCESSKEY!=null&&user!=null){
            mWeb.load("http://app.hekr.me/android/" + mid + "/index.html?access_key=" + Global.USERACCESSKEY + "&tid=" + tid + "&lang=" + lang+"&user="+user,null);
            //XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        }
        //mWeb.loadUrl("file:///android_asset/www/js/testws.html")
        Log.i("MyLog","detail-url:"+"http://app.hekr.me/android/" + mid + "/index.html?access_key=" + Global.USERACCESSKEY + "&tid=" + tid + "&lang=" + lang+"&user="+user);

    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public Map<Object, Object> getDetailMap(String detail)
    {

        List stateList = Util.tolist((Cell) Base.read.pc(detail, null));
        Map<Object, Object> detailMap=new HashMap<Object, Object>();
        try {
            for (int i = 0; i < stateList.size(); i = i + 2) {
                detailMap.put(stateList.get(i), stateList.get(i + 1));
            }
        }catch (Exception e){
            //e.printStackTrace();
        }
        return detailMap;
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
    //得到pid
    public String getPid(String detail)
    {
        String pid="";
        if(detail!=null){
            pid=getDetailMap(detail).get("pid")+"";
        }
        return pid;
    }
    public String getCid(String detail)
    {
        String cid="";
        if(detail!=null){
            cid=getDetailMap(detail).get("cid")+"";
        }
        return cid;
    }
    public String getMid(String detail)
    {
        String mid="";
        if(detail!=null){
            mid=getDetailMap(detail).get("mid")+"";
        }
        return mid;
    }
    public void navBack(View view)
    {
        this.finish();
    }
}
