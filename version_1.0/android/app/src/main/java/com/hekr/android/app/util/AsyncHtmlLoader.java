package com.hekr.android.app.util;

/**
 * Created by xiaomao on 2015/8/14.
 * desc:
 */
public class AsyncHtmlLoader {
    private static String CachePath = "/mnt/sdcard/Hekr/Html/";

    public static void saveHtml(final String pageId,final String Html_update_url){
        Runnable HtmlDownLoadSaveRunnable= new Runnable() {
            @Override
            public void run() {
                try {
                    //HttpHelper.saveZip(Html_update_url,CachePath+pageId+"/");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        HtmlDownLoadSaveRunnable.run();
    }
}
