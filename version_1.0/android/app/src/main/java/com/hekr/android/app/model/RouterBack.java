package com.hekr.android.app.model;

/**
 * Created by Lenovo on 2015/4/3.
 */
public class RouterBack//让热点设备连路由器返回的两个字段
{
    private String msg;//返回一个successfully
    private int code;//返回一个0

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
