package com.hekr.android.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.hekr.android.app.model.DeviceSummary;
import com.hekr.android.app.util.AssetsDatabaseManager;
import com.hekr.android.app.util.HekrUser;
import com.hekr.android.app.util.HttpHelper;
import com.hekr.android.app.util.MySettingsHelper;
import com.lambdatm.runtime.lang.Cell;
import com.lambdatm.runtime.lib.Base;
import com.lambdatm.runtime.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RenameDeviceActivity extends Activity implements OnClickListener{

    private EditText deviceNewNameEditText;//新名称
    private TextView deviceTypeTextView;//设备种类
    private TextView deviceNameTextView;//设备原名称
    private ImageButton updateNameButton;//确定修改
    private String oldTid;//原tid（唯一标识）
    private HekrUser hekrUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_device);
        deviceNewNameEditText= (EditText) findViewById(R.id.devicetid);
        Intent i=getIntent();
        oldTid=i.getStringExtra("tid");

        deviceTypeTextView=(TextView)findViewById(R.id.devicetype);
        deviceNameTextView=(TextView)findViewById(R.id.devicename);
        deviceTypeTextView.setText(getName(i.getStringExtra("detail")));
        if(!"".equals(oldTid)&&oldTid!=null){
            deviceNameTextView.setText(getResources().getString(R.string.device_oldname).toString()+oldTid.substring(oldTid.length() - 2));
        }

        updateNameButton=(ImageButton)findViewById(R.id.updatename);
        updateNameButton.setOnClickListener(this);
    }

    public String getName(String detail)
    {
        String cid="";
        if(detail!=null){
            cid=getDetailMap(detail).get("cid")+"";
        }

        if(cid=="")
        {
            return "unknow";
        }
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db = mg.getDatabase("db");
        String countryCategory=getResources().getConfiguration().locale.getCountry();
        Cursor cursor = null;
        if(countryCategory.equals("CN")){
            try {
                cursor = db.rawQuery("select name from category where id=?",
                        new String[]{cid});
                if (cursor.moveToNext()) {
                    return cursor.getString(0);
                }
                return "unknow";
            }catch (Exception e){
                return "unknow";

            }finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }
        else{
            try {
                cursor = db.rawQuery("select ename from category where id=?",
                        new String[]{cid});
                if (cursor.moveToNext()) {
                    return cursor.getString(0);
                }
                return "unknow";
            }catch (Exception e){
                return "unknow";

            }finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }

    }
    public static Map<Object, Object> getDetailMap(String detail)
    {
        if(!"".equals(detail)&&detail!=null){
            //Log.i("MyLog","detail:"+detail);
            List stateList = getDetailList(detail);
            if(stateList!=null&&stateList.size()>=2){
                Map<Object, Object> detailMap=new HashMap<Object, Object>();
                try {
                    for (int i = 0; i < stateList.size(); i = i + 2) {
                        detailMap.put(stateList.get(i), stateList.get(i + 1));
                    }
                }catch(Exception ex){
                }
                return detailMap;
            }
            return null;
        }
        return null;
    }
    public static List<Object> getDetailList(String detail) {
        try{

            if (!"".equals(detail) && detail != null) {
                List stateList = Util.tolist((Cell) Base.read.pc(detail, null));
                //Log.i("MyLog", "stateList:" + stateList);
                return stateList;
            }
        }catch (Exception e){
            //Log.i("MyLog","解析不了为list："+e.getMessage());
            return Util.tolist((Cell) Base.read.pc("(\"mid\" 0 \"pid\" 0 \"cid\" 0 )", null));
        }
        return Util.tolist((Cell) Base.read.pc("(\"mid\" 0 \"pid\" 0 \"cid\" 0 )", null));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rename_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void navBack(View view)
    {
        this.finish();
    }

    @Override
    public void onClick(View view) {
        if("".equals(deviceNewNameEditText.getText().toString().trim())||deviceNewNameEditText.getText().toString().trim()==null) {
            Toast.makeText(RenameDeviceActivity.this,getResources().getString(R.string.toast_newname_notnull).toString(),Toast.LENGTH_SHORT).show();
        }
        else{
            if(!"".equals(oldTid)&&oldTid!=null){
                hekrUser= HekrUser.getInstance(MySettingsHelper.getCookieUser());
                //hekrUser.renameDevice(oldTid, deviceNewNameEditText.getText().toString().trim());
                //Log.d("MyLog", "tid:"+oldTid+" 新名称:" +deviceNewNameEditText.getText().toString()+ " 改名返回值:"+hekrUser.renameDevice(oldTid,deviceNewNameEditText.getText().toString().trim()));
                //this.finish();
                new AsyncTask<Integer, Integer, Boolean>(){

                    @Override
                    protected Boolean doInBackground(Integer... integers) {
                            hekrUser.renameDevice(oldTid, deviceNewNameEditText.getText().toString().trim());
                            Log.d("MyLog", "tid:"+oldTid+" 新名称:" +deviceNewNameEditText.getText().toString()+ " 改名返回值:"+hekrUser.renameDevice(oldTid,deviceNewNameEditText.getText().toString().trim()));
                            return hekrUser.renameDevice(oldTid, deviceNewNameEditText.getText().toString().trim());
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if(aBoolean){
                            Toast.makeText(RenameDeviceActivity.this,"改名成功！",Toast.LENGTH_SHORT).show();
                            RenameDeviceActivity.this.finish();
                        }
                        else{
                            Toast.makeText(RenameDeviceActivity.this,"改名失败！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
            else{
                Toast.makeText(RenameDeviceActivity.this,"新名称不能为空！",Toast.LENGTH_SHORT).show();
            }

        }
    }
    //设置屏幕为竖屏
    protected void onResume()
    { /** * 设置为竖屏 */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        Log.i("LifeCycle", "RenameDeviceActivity--onResume()被触发");
        super.onResume();
    }
}
