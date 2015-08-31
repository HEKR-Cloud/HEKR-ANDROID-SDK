package com.hekr.android.app.model;

/**
 * Created by Lenovo on 2015/4/6.
 */
public class SetKeyBack {
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
