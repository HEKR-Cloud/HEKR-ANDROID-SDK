package com.hekr.android.app;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;


public class AboutOursActivity extends Activity {

    private TextView versionTextView;//显示版本号
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_ours);
        versionTextView= (TextView) findViewById(R.id.version);
        versionTextView.setText(getVersion().toString());
    }
    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
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

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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

    public void navBack(View view)
    {
        this.finish();
    }

}
