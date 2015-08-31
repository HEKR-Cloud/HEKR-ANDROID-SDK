package com.hekr.android.app.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kj on 15/6/18.
 */

public class HekrConfig {

    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    public static DatagramPacket[] encode(String data) throws IOException{
        DatagramSocket ds = new DatagramSocket();// 创建用来发送数据报包的套接字
        int len = data.getBytes("utf-8").length;
        DatagramPacket[] dps = new DatagramPacket[ len ];
        byte[] bs = data.getBytes();
        DatagramPacket  dp;
        for(int i=0 ; i<len ; i++ ){
            dp = new DatagramPacket( bs , len ,InetAddress.getByName("224."+i+"."+ unsignedByteToInt(bs[i]) +".255" ) , 7001 );
            dps[i] = dp;
            ds.send(dp);
        }
        dp = new DatagramPacket( bs, len , InetAddress.getByName("224."+127+"."+len+".255" ) , 7001 );
        ds.send(dp);
        ds.close();
        return dps;
    }

    /**
     * hekrconfig配置
     * @param ssid
     * @param password
     * @throws IOException
     */
    public static void hekrconfig(String ssid,String password ) throws IOException{
        hekrconfig(ssid, password, 0);
    }

    public static void hekrconfig(String ssid,String password,int time) throws IOException{
        DatagramSocket ds = new DatagramSocket();// 创建用来发送数据报包的套接字
        byte[] ssidbs = ssid.getBytes("utf-8");
        byte[] passbs = password.getBytes("utf-8");
        int len = ssidbs.length+passbs.length+2;
        byte[] d = "hekrconfig".getBytes("utf-8");
        DatagramPacket  dp;
        dp = new DatagramPacket( d  , d.length , InetAddress.getByName("224.127."+len+".255" ) , 7001 );
        ds.send(dp);

        byte[] data = (ssid+'\0'+password+'\0').getBytes() ;

        for(int i=0 ; i<data.length ; i++ ){
            dp = new DatagramPacket( d  , d.length,InetAddress.getByName("224."+i+"."+ unsignedByteToInt(data[i]) +".255" ) , 7001 );
            ds.send(dp);
            if(time>0){
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                }
            }
        }
        ds.close();
    }


    private String ak;
    public HekrConfig(String accesskey){
        this.ak = accesskey;
    }
    private boolean isConfigEnd=false;

    public void stop(){
        isConfigEnd = true;
    }

    public Object config(String ssid,String password){
        isConfigEnd = false;
        // 100 count loop send  udpdata

        for(int i = 0;i<100;i++){
            try {
                HekrConfig.hekrconfig(ssid + "", password + "" , 5 );
            } catch (IOException e){
            }
        }

        DatagramPacket dp = null;
        boolean configsuccess=false;
        int setakcount = 0;

        for(int i =0 ; i<40 ;i++) {
            try {
                //dp = UDPConfig.discover("","",800);
                dp = UDPConfig.waitDevice(1000);
                String str= new String(dp.getData());
                if(str.startsWith("(device ")) {
                    configsuccess = true;
                    break;
                }else if(str.startsWith("(deviceACK ")) {
                    configsuccess = true;
                    setakcount = 100;
                    break;
                }
            } catch (IOException e) {
                try {
                    dp = UDPConfig.setAccessKey( "255.255.255.255" , ak , null, 300);
                    String str= new String(dp.getData());
                    if(str.startsWith("(deviceACK ")) {
                        configsuccess = true;
                        setakcount = 100;
                        break;
                    }else if(str.startsWith("(device ")) {
                        configsuccess = true;
                        break;
                    }

                } catch (IOException e1) {
                }

                if(isConfigEnd){
                    break;
                }
            }
        }

        isConfigEnd= true;
        while( configsuccess ) {
            //超时重新发送
            if(setakcount>10){
                break;
            }
            try {
                dp = UDPConfig.setAccessKey(dp.getAddress().getHostAddress(), ak , null, 300);
                break;
            } catch (IOException ex) {
            }
            setakcount++;
        }

        if(configsuccess){
            return dp;
        }else{
            return null;
        }

    }

    public static boolean softapSetAccessKey(String accesskey){
        String respstr = HttpUtil.doGet( "http://192.168.10.1/t/set_ak?ak="+accesskey , null);
        return isSuccess(respstr);
    }

    public static boolean softapSetBridge(String ssid,String passwd){
        String respstr = HttpUtil.doGet("http://192.168.10.1/t/set_bridge?ssid=" + ssid + "&key=" + passwd, null);
        return isSuccess(respstr);
    }

    public static List softapList(){
        String respstr = HttpUtil.doGet("http://192.168.10.1/t/get_aplist", null);
        try {
            JSONArray ja = JSON.parseArray(respstr);
            return ja;
        }catch (Exception e){
            return null;
        }
    }

    public static boolean isSuccess(String respstr){
        try {
            if (respstr != null) {
                JSONObject jo = (JSONObject) JSONObject.parse(respstr);
                int code = jo.getInteger("code");
                if (code == 0) {
                    return true;
                }
            }
            return false;
        }catch (Exception ex){
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        for(int i =0 ;i<5000000 ; i++){
            hekrconfig("helloworld", "test" );
        }
    }
}