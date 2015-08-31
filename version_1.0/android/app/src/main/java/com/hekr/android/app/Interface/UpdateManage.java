package com.hekr.android.app.Interface;

/**
 * Created by xiaomao on 2015/8/13.
 * desc:
 */
public interface UpdateManage {
    public abstract String checkUpdate(String hash);
    //检查更新(增量) 自动安排下载策略
    //hash 最后一次更新的hash
}
