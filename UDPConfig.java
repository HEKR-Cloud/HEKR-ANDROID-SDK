package com.hekr.android.app.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDPConfig {


    public static DatagramPacket waitDevice(int timeout)throws IOException{
        DatagramSocket ds = new DatagramSocket(10000); // 创建用来发送数据报包的套接字
        //ds.setBroadcast(true);
        ds.setSoTimeout(timeout);
        byte[] bs = new byte[1024];
        DatagramPacket dp = new DatagramPacket(bs, bs.length);
        try {
            ds.receive(dp);
        }catch (IOException ex){
            throw ex;
        }finally {
            ds.close();
        }
        return dp;
    }

    /**
     * 发现设备
     * @param tid
     * @param option
     * @param timeout
     * @return
     * @throws IOException
     */
    public static DatagramPacket discover( String tid,String option,int timeout ) throws IOException{
        DatagramSocket ds = new DatagramSocket();// 创建用来发送数据报包的套接字
        ds.setSoTimeout(timeout);
        String data = "(discover \""+ tid +"\" \""+ option +"\" 10000 )";
        byte[] bs = data.getBytes();
        DatagramPacket dp = new DatagramPacket( bs , bs.length , InetAddress.getByName("255.255.255.255" ) , 10000 );
        ds.send(dp);
        dp.setData(new byte[1024]);
        //ds.receive(dp);
        ds.close();
        return dp;
    }
    /**
     * 设置accesskey
     * @param accesskey
     * @param tid 可以为空，则设置所有设备
     * @throws IOException
     */
    public static DatagramPacket setAccessKey(String ip,String accesskey,String tid,int timeout) throws IOException{
        DatagramSocket ds = new DatagramSocket(10000);// 创建用来发送数据报包的套接字
        String data = "";
        ds.setSoTimeout(timeout);
        if(tid == null){
            data= "(ak \""+ accesskey +"\" )";
        }else{
            data =  "(ak \""+ accesskey +"\" \""+tid+"\" )";
        }
        byte[] bs = data.getBytes();
        DatagramPacket dp = null;
        try {
            dp= new DatagramPacket(bs, bs.length, InetAddress.getByName(ip), 10000);
            ds.send(dp);
            dp.setData(new byte[1024]);
            ds.receive(dp);
        }catch (IOException ex){
            throw ex;
        }finally {
            ds.close();
        }
        return dp;
    }

    public static void main(String[] args) throws Throwable {
    }


}