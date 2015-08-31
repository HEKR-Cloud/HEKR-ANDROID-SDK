package com.hekr.android.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class PushDemoReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    public NotificationManager mNotificationManager;

    NotificationCompat.Builder mBuilder;
    /** Notification的ID */
    int notifyId = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        mNotificationManager=(NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        initNotify(context);

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");


                Log.d("payload","taskid:"+taskid);
                Log.d("payload","messageid:"+messageid);


                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
                Log.i("GetuiSdkDemo","result:"+result);

                if (payload != null) {
                    String data = new String(payload);
                    showIntentActivityNotify(context);
                    Log.d("GetuiSdkDemo", "receiver payload : " + data);

//                  Uri uri = Uri.parse("http://www.baidu.com");
//                  Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    //DeviceDetailActivity.mWeb.load("http://www.baidu.com",null);

                    //payloadData.append(data);
                    //payloadData.append("\n");
                    //Intent i=new Intent(context,DeviceDetailActivity.class);
                    //Log.d("GetuiSdkDemo","context:"+context+"i:"+i);
                    //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //context.startActivity(i);
                    //DeviceDetailActivity.mWeb.load("http://www.baidu.com",null);
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                break;

            case PushConsts.THIRDPART_FEEDBACK:

//               String appid = bundle.getString("appid");
//               String taskid = bundle.getString("taskid");
//               String actionid = bundle.getString("actionid");
//               String result = bundle.getString("result");
//               long timestamp = bundle.getLong("timestamp");
//               Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
//               taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
//               "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);

                break;

            //default:
                //break;
        }
    }
    /** 显示通知栏点击跳转到指定Activity */
    public void showIntentActivityNotify(Context context){
        // Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
        //notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        mBuilder.setAutoCancel(true)//点击后让通知将消失
                .setContentTitle("测试标题")
                .setContentText("点击跳转")
                .setTicker("点我");
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(context, SingleDeviceDetailActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    /** 初始化通知栏 */
    private void initNotify(Context context){
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                .setContentIntent(getDefalutIntent(context, Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.appicon);
    }
    public PendingIntent getDefalutIntent(Context context,int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, new Intent(), flags);
        return pendingIntent;
    }
}
