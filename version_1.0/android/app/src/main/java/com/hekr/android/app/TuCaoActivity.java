package com.hekr.android.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hekr.android.app.model.Global;
import com.hekr.android.app.ui.CustomProgress;
import com.hekr.android.app.util.HttpHelper;
import com.hekr.android.app.util.ThreadPool;
import com.hekr.android.app.util.WifiAdmin;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;

public class TuCaoActivity extends Activity {

    private EditText content;
    private ImageButton send;
    private CustomProgress tucaoProgressBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_help);
        content= (EditText) findViewById(R.id.tucaocontent);
        send= (ImageButton) findViewById(R.id.sendtucao);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = content.getText().toString();
                final String userAccessKey = Global.USERACCESSKEY;

                Runnable sendRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("back", HttpHelper.doPost("http://poseido.hekr.me/tucao.json", userAccessKey, message));
                        //Log.i("MyLog","runnable:backJson_str:"+HttpHelper.doPost("http://poseido.hekr.me/tucao.json", userAccessKey, message));
                        msg.setData(data);
                        sendHandler.sendMessage(msg);
                    }
                };
                if (!"".equals(content.getText().toString()) && content.getText().toString() != null) {
                    tucaoProgressBar = CustomProgress.show(TuCaoActivity.this, getResources().getString(R.string.sending_oba).toString(), true, null);
                    ThreadPool threadPool = ThreadPool.getThreadPool();
                    threadPool.addTask(sendRunnable);
                } else {
                    Toast.makeText(TuCaoActivity.this, getResources().getString(R.string.tucao_empty).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    Handler sendHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String backJson_str=data.getString("back");
            //Log.i("MyLog","backJson_str:"+backJson_str);
            if(tucaoProgressBar!=null){
                tucaoProgressBar.dismiss();
            }
            if(backJson_str!=null){
                try {
                    JSONObject backCodeJson = new JSONObject(backJson_str);
                    if(backCodeJson.getInt("code")==200){
                        Toast.makeText(TuCaoActivity.this,getResources().getString(R.string.oba_already_bb_success).toString(),Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(TuCaoActivity.this,getResources().getString(R.string.check_network).toString(),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TuCaoActivity.this,getResources().getString(R.string.check_network).toString(),Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(TuCaoActivity.this,getResources().getString(R.string.check_network).toString(),Toast.LENGTH_SHORT).show();
            }

        }
    };
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
    protected void onDestroy() {
        Log.i("LifeCycle", "TuCaoActivity--onDestroy()被触发");

        super.onDestroy();
    }

    public void navBack(View view)
    {
        this.finish();
    }
}
