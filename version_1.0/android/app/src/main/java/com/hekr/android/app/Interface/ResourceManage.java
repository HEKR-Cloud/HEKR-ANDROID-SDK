package com.hekr.android.app.Interface;

/**
 * Created by xiaomao on 2015/8/13.
 * desc:
 */
public interface ResourceManage {
    public abstract String templatePath(String templateId);
    //获取模板本地地址
    //templateId 模板ID

    public abstract boolean saveTemplate(String templateId,String path);
    //从临时文件保存到本地模板
    //templateId 模板ID
    //path 临时文件路径
}
