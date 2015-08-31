package com.hekr.android.app.model;

/**
 * Created by Lenovo on 2015/4/4.
 */
//存放访问了生成keyAPI之后用来封装一些返回信息的类
public class KeyBack {
    private String uid;
    private Long time;
    private String type;
    private String token;//这个需要用到，提供给设置热点key的参数中

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }


    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

}
