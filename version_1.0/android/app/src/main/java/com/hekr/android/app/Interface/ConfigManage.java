package com.hekr.android.app.Interface;

import java.util.HashMap;

/**
 * Created by xiaomao on 2015/8/13.
 * desc:
 */
public interface ConfigManage {
    public abstract String lastTemplatesHash();
    //最后一次更新的hash `需跟服务器端商定`
    //ret hash

    public abstract void saveLastTemplatesHash();
    //保存最后一次更新的hash

    public abstract HashMap<String,String> lastTokens();
    //最后一次用户登录时的token信息
    //ret 客户端要用到的token

    public abstract void saveLastTokens(HashMap<String,String> hashMap);
    //保存最后用户登录的token
    //tokens 客户端token
}
