package com.hekr.android.app.model;

import java.io.Serializable;

/**
 * Created by xubukan on 2015/3/27.
 */
public class DeviceSummary implements Serializable
{
    private static final long serialVersionUID = -7620435178023928252L;
    private String uid;
    private Integer online;//是否在线
    private Long time;
    private String detail;//厂家id,设备id...
    private String tid;//设备唯一标识
    private String product_img;
    private String producer_img;
    private String name;//有些设备没有，应该是表示什么类型的设备，例如浴霸
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public String getProducer_img() {
        return producer_img;
    }

    public void setProducer_img(String producer_img) {
        this.producer_img = producer_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DeviceSummary{" +
                "uid='" + uid + '\'' +
                ", online=" + online +
                ", time=" + time +
                ", detail='" + detail + '\'' +
                ", tid=" + tid +
                ", product_img='" + product_img + '\'' +
                ", producer_img='" + producer_img + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
