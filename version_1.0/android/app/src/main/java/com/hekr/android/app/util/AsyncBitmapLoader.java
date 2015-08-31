package com.hekr.android.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.util.Log;

import java.io.*;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by xubukan on 2015/3/29.
 */
public class AsyncBitmapLoader
{
    /**
     * 内存图片软引用缓冲
     */
    private static String CachePath = "/mnt/sdcard/Hekr/";

    public AsyncBitmapLoader()
    {
        //imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public static Bitmap SaveBitmap(final String imageURL) {
        // 在http://user.hekr.me/res/api/categories.json中截取出来的 "/images/logo/categories/icon_01@3x.png"
        //处理一下变成"images/logo/categories/icon_01@3x.png"
        //String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);

        Runnable iconDownRunnable = new Runnable() {

            public void run() {
                InputStream bitmapIs = HttpHelper.getStreamFromURL(imageURL);
                Bitmap bitmap=null;
                if(bitmapIs!=null){
                    bitmap = BitmapFactory.decodeStream(bitmapIs);
                }
                FileOutputStream fos = null;
                File dir = new File(CachePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File bitmapFile = new File(CachePath +
                        imageURL.substring(imageURL.lastIndexOf("/") + 1));
                try {
                    if(!bitmapFile.exists()){
                        try {
                            bitmapFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    fos = new FileOutputStream(bitmapFile);
                    if(bitmap!=null){
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);// 把数据写入文件
                    }
                } catch (Exception e) {
                    Log.d("MyLog","save图片时流转换成bitmap出错:"+e.getMessage());
                } finally {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        };
        iconDownRunnable.run();
        return null;
    }
}
