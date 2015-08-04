package com.hekr.android.app.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kj on 15/7/24.
 */
public class HekrUser {
    private Random r = new Random();
    private String ucookie;
    //private String _csrftoken_ = "_csrftoken_=abcd";
    private String _csrftoken_ = getRandomString(4);
    private String cookie;
    public  String httphostcn = "http://user.hekr.me";
    public  String httphost = null;

    public static interface HekrCallBackHandle{
        public void callback(long randomid,Object obj);
    }

    private static HekrUser hekrUser = null;
    public static synchronized HekrUser getInstance(String ucookie) {
        if (hekrUser == null) {
            hekrUser = new HekrUser(ucookie);
        }
        return hekrUser;
    }

    public HekrUser(String ucookie){
        r.setSeed(System.currentTimeMillis());
        this.httphost = httphostcn;
        this.ucookie = ucookie;
        this.cookie = ucookie+";"+_csrftoken_;
    }

    private String usertid;
    private String userAccessKey;
    private String deviceAccessKey;
    private String host = "device.hekr.me";
    private int port = 9999;

    public void setTid(String tid){
        this.usertid = tid;
    }

    public boolean generateAccessKey(){
        try {
            String respstr = HttpUtil.doGet( httphost+"/token/generate.json?type=DEVICE&" + _csrftoken_, cookie);
            JSONObject jo = JSONObject.parseObject(respstr);

            deviceAccessKey = jo.get("token") + "";
            respstr = HttpUtil.doGet( httphost+"/token/generate.json?type=USER&" + _csrftoken_, cookie);

            jo = JSONObject.parseObject(respstr);
            userAccessKey = jo.get("token") + "";

            return true;
        }catch (Exception ex){
            return false;
        }
    }


    public List list(){
        Map m = null;
        String respstr = HttpUtil.doGet( httphost+"/device/list.json?" + _csrftoken_, cookie);
        if(respstr != null) {
            JSONArray j = JSON.parseArray(respstr);
            return j;
        }else {
            return null;
        }
    }

    public void list(HekrCallBackHandle callback){
        Map m = null;
        String respstr = HttpUtil.doGet( httphost+"/device/list.json?" + _csrftoken_, cookie);
        if(respstr != null) {
            JSONArray j = JSON.parseArray(respstr);
            callback.callback( r.nextLong() , j );
        }else {
            callback.callback( r.nextLong() , null );
        }
        return ;
    }
    //删除
    public boolean deviceDelete(String tid){
        String respstr = HttpUtil.doGet( httphost+"/device/delete.json?tid="+tid +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    //改名
    public boolean deviceRename(String tid,String name){
        String respstr = HttpUtil.doGet( httphost+"/device/rename/"+tid+".json?name="+name +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    public boolean deviceActivate(String encryptkey,String ver,long time){
        String respstr = HttpUtil.doGet( httphost+"/device/activate.json?encryptkey="+encryptkey+"&ver="+ver+"&time="+time+"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    public boolean folderCreate(String name){
        String respstr = HttpUtil.doGet( httphost+"/folder/create.json?name="+name
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    public boolean folderDelete(String fid){
        String respstr = HttpUtil.doGet( httphost+"/folder/delete/"+fid+".json?"
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    public boolean folderRename(String fid,String fname){
        String respstr = HttpUtil.doGet( httphost+"/folder/rename/"+fid+".json?name="+fname
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    public boolean folderAdd(String fid,String tid){
        String respstr = HttpUtil.doGet( httphost+"/folder/"+fid+"/add/"+tid+".json?"
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    public boolean folderRemove(String fid,String tid){
        String respstr = HttpUtil.doGet( httphost+"/folder/"+fid+"/remove/"+tid+".json?"
                +"&" + _csrftoken_ , cookie);
        return isSuccess(respstr);
    }

    public String getCsrftoken(){
        return _csrftoken_;
    }

    public String getUserAccessKey(){
        return userAccessKey;
    }

    public String getDeviceAccessKey(){
        return deviceAccessKey;
    }

    public String getCookie(){
        return cookie;
    }

    public boolean devcall(String tid,String code){
        String c = "(@devcall \""+tid+"\"  "+code+" (lambda x x) )";
        //  todo
        return false;
    }

    public boolean devcallUartData(String tid,String uartdata){
        String code = "(@devcall \""+tid+"\" (uartdata \""+uartdata+"\") (lambda x x) )";
        // todo
        return true;
    }

    public static boolean isSuccess(String respstr){
        try {
            if (respstr != null) {
                JSONObject jo = (JSONObject) JSONObject.parse(respstr);
                int code = jo.getInteger("code");
                if (code == 200) {
                    return true;
                }
            }
            return false;
        }catch (Exception ex){
            Log.d("MyLog","调用删除异常");
            return false;
        }
    }
    private static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
