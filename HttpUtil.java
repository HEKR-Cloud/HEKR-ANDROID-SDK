package com.hekr.android.app.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * Created by kj on 15/7/24.
 */
public class HttpUtil {

    private static HttpParams httpParameters;
    private static int timeoutConnection = 15000;
    private static int timeoutSocket = 15000;


    public static String doGet(String url,String cookie){
        HttpGet http = new HttpGet(url);
        if(cookie != null) {
            http.addHeader("cookie", "u="+cookie);
        }
        try{
            HttpResponse res = new DefaultHttpClient().execute(http);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = res.getEntity();
                return EntityUtils.toString(entity, HTTP.UTF_8);
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    public static String doPost(String url , String cookie , String postdata){
        HttpPost http = new HttpPost(url);
        if(cookie != null) {
            http.addHeader("cookie", cookie);
        }
        try{
            DefaultHttpClient dhc = new DefaultHttpClient();
            //http.setEntity();

            // todo...
            HttpResponse res = dhc.execute(http);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = res.getEntity();
                return EntityUtils.toString(entity, HTTP.UTF_8);
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

}
