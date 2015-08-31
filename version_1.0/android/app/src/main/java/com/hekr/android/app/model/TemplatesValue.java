package com.hekr.android.app.model;

/**
 * Created by xiaomao on 2015/8/21.
 * desc:
 */
public class TemplatesValue {
    private String op;
    private String name;
    private String version;
    private String hash;
    private String url;

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TemplatesValue{" +
                "op='" + op + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", hash='" + hash + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
