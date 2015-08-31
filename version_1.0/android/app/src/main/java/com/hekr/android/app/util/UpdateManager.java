package com.hekr.android.app.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.util.Log;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hekr.android.app.R;
import com.hekr.android.app.model.Global;

/**
 * Created by Lenovo on 2015/4/8.
 */
public class UpdateManager
{
    private Context mContext;

    //提示语
    private String updateMsg = "有最新的软件包哦，亲快下载吧~";

    //返回的安装包url
    private String apkUrl =Global.apkUrl;

    private Dialog noticeDialog;

    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private String savePath = Global.savePath;

    private String saveFileName = savePath + apkUrl.substring(apkUrl.lastIndexOf("/")+1);

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private TextView present;


    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    present.setText(progress+"%");
                    break;
                case DOWN_OVER:

                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    //外部接口让主Activity调用
    public void checkUpdateInfo(){
        showNoticeDialog();
    }


    private void showNoticeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.software_update).toString());
        builder.setMessage(mContext.getResources().getString(R.string.have_new_software_download_go).toString());
        builder.setPositiveButton(mContext.getResources().getString(R.string.download).toString(), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.see_you_later).toString(), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        if(noticeDialog!=null){
            noticeDialog.show();
        }
    }

    private void showDownloadDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.software_version_update).toString());

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progress);
        present= (TextView) v.findViewById(R.id.present);

        builder.setView(v);
        builder.setNegativeButton(mContext.getResources().getString(R.string.update_cancel).toString(), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        if(downloadDialog!=null){
            downloadDialog.show();
        }
        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if(!file.exists()){
                    file.mkdir();
                }
                String apkFile = saveFileName;
                FileOutputStream fos=null;
                File ApkFile = new File(apkFile);
                try {
                    if (!ApkFile.exists()) {
                        try {
                            ApkFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do {
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        //更新进度
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                        if (numread <= 0) {
                            //下载完成通知安装
                            mHandler.sendEmptyMessage(DOWN_OVER);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!interceptFlag);//点击取消就停止下载.
                }catch (FileNotFoundException e){
                    Log.i("MyLog","FileNotFoundException创建出错："+e.getMessage());
                }finally {
                    fos.close();
                    is.close();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("MyLog","MalformedURLException下载出错："+e.getMessage());

            } catch(IOException e){
                Log.i("MyLog","IOException下载出错："+e.getMessage());
                e.printStackTrace();
            }


        }
    };

    /**
     * 下载apk
     * @param
     */

    private void downloadApk(){
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    /**
     * 安装apk
     * @param
     */
    private void installApk(){
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);

    }
}

