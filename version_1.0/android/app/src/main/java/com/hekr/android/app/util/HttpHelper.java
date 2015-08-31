package com.hekr.android.app.util;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by xubukan on 2015/3/23.
 */
public class HttpHelper {
    final static String TAG="MyLog";
    static String seed = getRandomString(4);
    //static String seed = "abcd";

    private static HttpParams httpParameters;
    private static int timeoutConnection = 15000;
    private static int timeoutSocket = 15000;

    public static String doGet(String url)
    {
        HttpGet http = new HttpGet(url+"_csrftoken_="+seed);
        //Log.d("MyLog","seed:"+seed);
        String CookieUser=MySettingsHelper.getCookieUser();
        //Log.i("coco","CookieUser:"+CookieUser);
        http.addHeader("cookie","u="+CookieUser+";_csrftoken_="+seed);
        try{
            HttpResponse res = new DefaultHttpClient().execute(http);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = res.getEntity();
                //json = new JSONObject(new JSONTokener(new InputStreamReader(entity.getContent(), HTTP.UTF_8)));

                return EntityUtils.toString(entity, HTTP.UTF_8);
                //InputStreamReader reader = new InputStreamReader(entity.getContent(), HTTP.UTF_8);
                //reader.read();
            }
        }catch (Exception e){
            Exception ex = e;
            Log.d("MyLog","HttpGet请求出错--:"+e.getMessage());
            e.printStackTrace();
            return null;
        }

        return null;
    }

    //让热点设备连接路由器已经迁徙到AddDeviceActivity
    public static String doGet(String name,String password,int channel,String bssid,String encryption){
//      String str="http://192.168.10.1/t/set_bridge?ssid="+name+"&channel=1&bssid=50%3ABD%3A5F%3A6B%3A1D%3AF4&encryption=psk2&key="+password;
        Log.i("HttpLog","bssid为:"+bssid);
        try {
            bssid = URLEncoder.encode(bssid,"utf-8");
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
        HttpGet http = new HttpGet(str);
        httpParameters = new BasicHttpParams();// Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        //http.setParams(httpParameters);
        //http.addHeader("Content-Type", "text/html");

        DefaultHttpClient dhc = new DefaultHttpClient(httpParameters);
        //HttpClient dhc = new DefaultHttpClient(httpParameters);
        //HttpClient dhc = new HttpClient(httpParameters,new SimpleHttpConnectionManager(true) );
//      HttpClient client = new HttpClient(httpParameters,new SimpleHttpConnectionManager(true));


        try{
            HttpResponse res = dhc.execute(http);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = res.getEntity();
                //json = new JSONObject(new JSONTokener(new InputStreamReader(entity.getContent(), HTTP.UTF_8)));

                return EntityUtils.toString(entity, HTTP.UTF_8);
                //InputStreamReader reader = new InputStreamReader(entity.getContent(), HTTP.UTF_8);
                //reader.read();
            }
        }catch (Exception e){
            Log.d(TAG,"设置路由器java.net.SocketException异常："+e.toString());
            if(e instanceof java.net.SocketException){

                HttpResponse res = null;
                try {
                    res = dhc.execute(http);
                    if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                    {
                        HttpEntity entity = res.getEntity();
                        //json = new JSONObject(new JSONTokener(new InputStreamReader(entity.getContent(), HTTP.UTF_8)));

                        return EntityUtils.toString(entity, HTTP.UTF_8);
                        //InputStreamReader reader = new InputStreamReader(entity.getContent(), HTTP.UTF_8);
                        //reader.read();
                    }
                } catch (IOException e1) {
                    Log.d(TAG, "设置路由器java.net.SocketException二次异常：" + e.toString());
                }

            }
            return null;
        }

        return null;
    }

    //在热点设备上设置生成的key值
    public static String doGet(String ak,int i){
        try{
            String str="http://192.168.10.1/t/set_ak?ak="+ak;
            HttpGet http = new HttpGet(str);
            HttpResponse res = new DefaultHttpClient().execute(http);

            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = res.getEntity();
                //json = new JSONObject(new JSONTokener(new InputStreamReader(entity.getContent(), HTTP.UTF_8)));

                return EntityUtils.toString(entity, HTTP.UTF_8);
                //InputStreamReader reader = new InputStreamReader(entity.getContent(), HTTP.UTF_8);
                //reader.read();
            }
        }catch (Exception e){
            Exception ex = e;
            Log.d(TAG,"设置key异常："+e.toString());
            return null;
        }
        return null;
    }
    //从服务器下载图片
    public static InputStream getStreamFromURL(String imageURL)
    {
        InputStream in=null;
        try {
            URL url=new URL(imageURL);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            //connection.setConnectTimeout(6000);// 超时时间6秒
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200){
                in=connection.getInputStream();
                //Log.d("MyLog","请求=200执行：in=connection.getInputStream()");
                return in;
            }
            else{
                Log.d("MyLog", "服务器无响应，返回状态值不等于200");
                return null;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("MyLog","in---:"+e.getMessage()+"-- "+imageURL);
            //e.printStackTrace();
            return null;
        }
    }
    //下载网页
    public static void saveZip(String path,String filepath) throws Exception{
        URL url = new URL(path);//服务器url
        String fileName = new File(url.getFile()).getName();
        Log.d("MyLog","fileName:"+fileName);
        File file=null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 得到sdcar文件目录
            File dir = Environment.getExternalStorageDirectory();
            Log.d("MyLog","dir:"+dir);
            file=new File("/mnt/sdcard/HekrHtml/");//新建一个file文件
            Log.d("MyLog","file:"+file.getName()+":"+file.getPath()+":"+file.exists()+":"+file.getAbsolutePath());
            if (!file.exists()) {
                file.mkdirs();
                Log.d("MyLog","file:"+file.getName()+":"+file.getPath()+":"+file.exists()+":"+file.getAbsolutePath());
            }
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();//从网络获得链接
        connection.setRequestMethod("GET");//获得
        //connection.setConnectTimeout(15000);//设置超时时间为5s

        if(connection.getResponseCode()==200)//检测是否正常返回数据请求 详情参照http协议
        {
            InputStream is = connection.getInputStream();//获得输入流

            Log.d("MyLog","is:"+is.toString());

            FileOutputStream out = new FileOutputStream(file);//对应文件建立输出流

            byte[] buffer = new byte[1024];
            //BufferedInputStream in = new BufferedInputStream(is, 1024*8);
            //BufferedOutputStream out  = new BufferedOutputStream(fos, 1024*8);

            int len = 0;
            try {
                while ((len = is.read(buffer)) != -1) {//当没有读到最后的时候
                    out.write(buffer, 0, len);//将缓存中的存储的文件流秀娥问file文件
                }
                out.flush();//将缓存中的写入file
            }catch (IOException e){
                e.printStackTrace();
            }
            finally
            {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }
    //得到设备
    public static String getUserDevice(){
        String url = "http://user.hekr.me/device/list.json?";

        return doGet(url);
    }
    //获取更新apk的url
    public static String getApkUrl(String updateUrl)
    {

        URL url= null;
        try {
            url = new URL(updateUrl);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5*1000);
            // 超时时间6秒
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                String apkurl = "";
                while ((inputLine = in.readLine()) != null)
                {
                    apkurl += inputLine;
                }
                return apkurl;
            }
            else{
                Log.d("MyLog", "服务器无响应");
                return null;
            }
        } catch (Exception e) {
            Log.d("MyLog","app--http报异常"+e.getMessage());
            return null;
        }

    }
    //send吐槽
    public static String doPost(String uriApI,String userAccessKey,String content)
    {

        String result = "";
        HttpPost httpRequst = new HttpPost(uriApI);//创建HttpPost对象

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userAccessKey", userAccessKey));
        params.add(new BasicNameValuePair("content", content));

        try {
            httpRequst.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
            if(httpResponse.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);//取出应答字符串
            }
        } catch (UnsupportedEncodingException e) {
            Log.i("MyLog", e.getMessage().toString());
            return null;
        }
        catch (ClientProtocolException e) {
            Log.i("MyLog", e.getMessage().toString());
            return null;
        }
        catch (IOException e) {
            Log.i("MyLog", e.getMessage().toString());
            return null;
        }
        return result;
    }


    public static String getProductIconList(){
        String url = "http://poseido.hekr.me/appcategories.json?";
        //String url = "http://192.168.1.81:8080/poseido/appcategories.json?";
        return doGet(url);
    }

    //删除
    public static boolean deviceDelete(String tid){
        return isSuccess(doGet("http://user.hekr.me/device/delete.json?tid="+tid +"&"));
    }

    //改名
    public static boolean deviceRename(String tid,String name){
        String respstr = doGet("http://user.hekr.me/device/rename/"+tid+".json?name="+name +"&");
        return isSuccess(respstr);
    }

    public static String getProviderIconList(){
        String url = "http://smartmatrix.mx/res/api/providers.json?";

        return doGet(url);
    }

    public static String getAPList(){
        String url = "http://192.168.10.1/cgi-bin/luci/zeroplus/mode/get_aplist";

        return doGet(url);
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

    public static boolean isSuccess(String respstr){
        try {
            if (respstr != null) {
                com.alibaba.fastjson.JSONObject jo = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(respstr);
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


    public static String getUpdateTestJson()
    {
        URL url= null;
        try {
            url = new URL("http://nj02.poms.baidupcs.com/file/d373d9152d161101303fb6d17d95670a?bkt=p2-nb-929&fid=203316868-250528-176723554385110&time=1440724341&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-xK4ffP3XhETQnhTC6VY20zU2864%3D&to=n2b&fm=Nin,B,U,ny&sta_dx=0&sta_cs=0&sta_ft=txt&sta_ct=1&fm2=Ningbo,B,U,ny&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400d373d9152d161101303fb6d17d95670acedd144b0000000002cc&sl=71237711&expires=8h&rt=sh&r=896067011&mlogid=865890158&vuk=-&vbdid=3150609193&fin=testJson.txt&fn=testJson.txt&slt=pm&uta=0&rtype=1&iv=0&isw=0");
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5*1000);
            // 超时时间6秒
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                String backJson = "";
                while ((inputLine = in.readLine()) != null)
                {
                    backJson += inputLine;
                }
                //Log.d("MyLog","backJson:"+backJson);
                return backJson;
            }
            else{
                Log.d("MyLog", "服务器无响应");
                return null;
            }
        } catch (Exception e) {
            Log.d("MyLog", "app--http报异常" + e.getMessage());
            return null;
        }
    }

    public static InputStream getHtml(String htmlURL)
    {
        InputStream in=null;
        try {
            URL url=new URL(htmlURL);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            //connection.setConnectTimeout(6000);// 超时时间6秒
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200){
                in=connection.getInputStream();
                Log.d("MyLog","请求=200执行：in="+in);
                return in;
            }
            else{
                Log.d("MyLog", "服务器无响应，返回状态值不等于200");
                return null;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("MyLog","in---:"+e.getMessage()+"-- "+htmlURL);
            //e.printStackTrace();
            return null;
        }
    }
    //静态页面模板
    public static String doPostCheckPage(String pageUrl,String userAccessKey)
    {
        HttpPost httpRequst = new HttpPost(pageUrl);//创建HttpPost对象
        String CookieUser=MySettingsHelper.getCookieUser();
        //Log.i("MyLog","CookieUser:"+CookieUser);
        httpRequst.addHeader("cookie","u="+CookieUser+";_csrftoken_="+seed);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("_csrftoken_", seed));
        params.add(new BasicNameValuePair("userAccessKey", userAccessKey));

        try {
            httpRequst.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
            if(httpResponse.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity httpEntity = httpResponse.getEntity();
                return EntityUtils.toString(httpEntity);//取出应答字符串
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            Log.i("MyLog", e.getMessage().toString());
            return null;
        }
        catch (ClientProtocolException e) {
            Log.i("MyLog", e.getMessage().toString());
            return null;
        }
        catch (IOException e) {
            Log.i("MyLog", e.getMessage().toString());
            return null;
        }
    }
}
