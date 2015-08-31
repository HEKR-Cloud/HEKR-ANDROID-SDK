package com.hekr.android.app.model;

/**
 * Created by xubukan on 2015/3/28.
 */
public class APEnctryption {
    private boolean enabled;
    private Object auth_algs;
    private String description;
    private boolean wep;
    private String[] auth_suites;//api有
    private Integer wpa;
    private String[] pair_ciphers;
    private String[] group_ciphers;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Object getAuth_algs() {
        return auth_algs;
    }

    public void setAuth_algs(Object auth_algs) {
        this.auth_algs = auth_algs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWep() {
        return wep;
    }

    public void setWep(boolean wep) {
        this.wep = wep;
    }

    public String[] getAuth_suites() {
        return auth_suites;
    }

    public void setAuth_suites(String[] auth_suites) {
        this.auth_suites = auth_suites;
    }

    public Integer getWpa() {
        return wpa;
    }

    public void setWpa(Integer wpa) {
        this.wpa = wpa;
    }

    public String[] getPair_ciphers() {
        return pair_ciphers;
    }

    public void setPair_ciphers(String[] pair_ciphers) {
        this.pair_ciphers = pair_ciphers;
    }

    public String[] getGroup_ciphers() {
        return group_ciphers;
    }

    public void setGroup_ciphers(String[] group_ciphers) {
        this.group_ciphers = group_ciphers;
    }
}
