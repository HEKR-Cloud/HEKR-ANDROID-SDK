package com.hekr.android.app.model;



/**
 * Created by xiaomao on 2015/8/21.
 * desc:
 */
public class TemplatesInfo {
    private int code;
    private String message;
    private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TemplatesInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", value=" + value +
                '}';
    }
}
