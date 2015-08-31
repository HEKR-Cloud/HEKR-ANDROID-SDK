package com.hekr.android.app.Interface;

import java.util.List;

/**
 * Created by xiaomao on 2015/8/13.
 * desc:
 */
public interface DeviceManage {
    public boolean isSuccess(String respstr);
    //检查关于设备操作(增、删、改、查之类的)返回字符串的检查code是否等于200
    //返回值 code=200 true 否则false

    public abstract List listDevice();
    //列举设备

    public abstract boolean removeDevice(String tid);
    //删除设备
    //tid 设备的tid

    public abstract boolean renameDevice(String tid,String name);
    //设备改名
    //tid 设备的tid
    //name 新名字

    public abstract boolean activateDevice(String encryptkey,String ver,long time);
    //激活设备(无属主登录情况下需要主动认领设备)
    //encryptkey 加密钥匙（服务端会根据加密钥匙获取具体哪个设备，然后把设备关联到具体用户中）
    //ver api版本,目前只支持1.0
    //time 由用户输入设备大概上线时间，格式unixtime（服务端会根据上线时间前后范围内查找）

    public abstract boolean folderCreate(String name);
    //新增目录
    //name 目录名

    public abstract List folderList();
    //列举目录
    //name 目录名

    public abstract boolean folderDelete(String fid);
    //删除目录
    //fid 目录id

    public abstract boolean folderRename(String fid,String fname);
    //重命名目录
    //fid 目录id
    //fname 新目录名

    public abstract boolean folderAdd(String fid,String tid);
    //把设备添加到目录
    //fid 目录id
    //tid 设备id

    public abstract boolean folderRemove(String fid,String tid);
    //把设备从子目录移到根目录
    //fid 目录id
    //tid 设备id

    //public abstract void control(Device device,String state,Value value);
    //设备控制
    //device 设备对象
    //state 要控制的属性
    //value 新值 Bool/Number/String
}
