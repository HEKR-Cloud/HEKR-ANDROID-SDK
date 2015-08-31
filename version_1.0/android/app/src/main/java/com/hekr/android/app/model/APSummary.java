package com.hekr.android.app.model;

/**
 * Created by xubukan on 2015/3/28.
 */
public class APSummary //路由器信息类
{
    private String auth_suites;
    private String ssid;//api有
    private Integer channel;//api有
    private Integer signal;//api有
    private String bssid;//api有


    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getSignal() {
        return signal;
    }

    public void setSignal(Integer signal) {
        this.signal = signal;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
    public String getAuth_suites() {
        return auth_suites;
    }

    public void setAuth_suites(String auth_suites) {
        this.auth_suites = auth_suites;
    }

}
