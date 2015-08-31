package com.hekr.android.app.Interface;

import java.net.DatagramPacket;
import java.util.List;

/**
 * Created by xiaomao on 2015/8/13.
 * desc:
 */
public interface DeviceConfig {
    public abstract void hekrconfig(String ssid ,String password ,float mtime);
    //ssid 必选；wifi ssid
    //password 必选；wifi 密码
    //mtime 可选；毫秒时间，每隔n个毫秒发送一个数据包；延缓路由器压力。

    public abstract Object config(String ssid,String password);
    //一键配置Wi-Fi，此方法调用会进入循环,代码会阻塞状态
    //ssid ssid
    //password 密码
    //Object为null 连接失败

    public abstract  DatagramPacket waitDevice(int timeout);
    //等待设备连接路由器后发送数据
    //返回值DatagramPacket udp数据包
    //timeout 等待时间(毫秒)

    public abstract DatagramPacket setAccessKey(String ip,String accesskey,String tid,int timeout);
    //局域网内对设备设置accesskey
    //返回值DatagramPacket udp数据包
    //ip ip地址
    //accesskey 设置设备的accesskey
    //tid 设备唯一ID
    //mtime 等待超时毫秒

    public abstract DatagramPacket discover(String tid,String option,int timeout);
    //内网发现设备
    //返回值DatagramPacket udp数据包
    //tid tid 设备唯一id
    //option 可选，填写 ""
    //timeout 发现设备超时时间

    public abstract boolean softapSetBridge(String ssid,String password);
    //软ap 连接路由
    //ssid ssid
    //password Wi-Fi 密码

    public abstract boolean softapSetAccessKey(String accesskey);
    //软ap 设置accesskey
    //accesskey 设备的accesskey

    public abstract List softapList();
    //获取WIFI列表
}
