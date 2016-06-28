# HEKR 3.0 SDK Android开发指南
## 集成准备
* [Java JDK] [0]
* [Android SDK/Android Stuido][1]

###1、快速导入SDK

* Please ensure that you are using the latest version by [checking here](https://jcenter.bintray.com/me/hekr/hekrsdk/hekrsdk/)

* Gradle:
```
    compile 'me.hekr.hekrsdk:hekrsdk:0.0.3'
```
* Maven:
```
<dependency>
  <groupId>me.hekr.hekrsdk</groupId>
  <artifactId>hekrsdk</artifactId>
  <version>0.0.3</version>
  <type>pom</type>
</dependency>
```
* [Or download hekrSDK from Maven Central](https://jcenter.bintray.com/me/hekr/hekrsdk/hekrsdk/)

##一、配置
* 说明：本SDK中已使用以下依赖，请勿重复配置！
```
android-async-http-1.4.9.jar
annotations-java5-15.0.jar
eventbus-3.0.0.jar
fastjson-1.1.51.android.jar
httpclient-4.4.1.2.jar
jmdns-3.2.2.jar
lite-common-1.1.3.jar
websocket.jar
zxing.jar
//第三方登录 qq/wechat/weibo
libammsdk.jar
mta-sdk-1.6.2.jar
weiboSDKCore_3.1.2.jar
open_sdk_r5509.jar
```

* 在项目res目录下创建raw目录,将下载包中的config.json、webviewjavascriptbridge.js复制进去，config.json为项目的配置文件和第三方登录配置文件（填写各大平台申请的参数），webviewjavascriptbridge.js为控制页面桥接js。
* 如果需要第三方微信登录,则必须将下载包中的wxapi文件夹复制项目包名目录（微信开放平台填写的包名）下！【具体参考[微信开放平台文档][11]】
 
###1.1、设置AndroidManifest.xml声明使用权限和服务
```
<!-- 这个权限用于进行网络定位-->
<uses-permission android:name="android.permission.INTERNET" />
<!-- 这个权限用于进行获取网络状态-->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<!-- 这个权限用于进行wifi组播-->
<uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE" />
<!-- 这些权限用于进行二维码扫描-->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.FLASHLIGHT" />
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />
<!-- 这个权限用于进行配网时阻止屏幕休眠-->
<uses-permission android:name="android.permission.WAKE_LOCK" />
<!-- 这个权限用于获取wifi的获取权限-->
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<!-- 用于读取手机当前的状态-->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<!-- 写入扩展存储，向扩展卡写入数据，用于写入用户数据-->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


<!-- web控制服务-->
<service android:name="me.hekr.hekrsdk.service.WebSocketService" />
<!-- 局域网发现服务-->
<service android:name="me.hekr.hekrsdk.service.DiscoveryService" />
<!-- 局域网控制服务-->
<service android:name="me.hekr.hekrsdk.service.LANService" />


<!--第三方登录 如果需要第三方登录则配置，不需要则不用配置-->
<activity android:name="me.hekr.hekrsdk.HekrOAuthLoginActivity" />

<!--第三方登录 qq 如果需要qq登录则配置，不需要则不用配置-->
<activity
    android:name="com.tencent.tauth.AuthActivity"
    android:launchMode="singleTask"
    android:noHistory="true"
    android:screenOrientation="portrait">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <!--此处为腾讯开放平台申请的ApiId-->
            <data android:scheme="tencent0000000000" />
        </intent-filter>
</activity>
<activity
    android:name="com.tencent.connect.common.AssistActivity"
    android:configChanges="orientation|keyboardHidden|screenSize"
    android:screenOrientation="portrait" />
    
<!--第三方登录 weibo 如果需要weibo登录则配置，不需要则不用配置-->
 <activity
    android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser"
    android:configChanges="keyboardHidden|orientation"
    android:exported="false"
    android:screenOrientation="portrait"
    android:windowSoftInputMode="adjustResize" />
<!--第三方登录 wechat 如果需要wechat登录则配置，不需要则不用配置-->
<activity
    android:name=".wxapi.WXEntryActivity"
    android:exported="true"
    android:screenOrientation="portrait">
        <intent-filter>
             <category android:name="android.intent.category.default" />
        </intent-filter>
</activity>
```
###1.2、在项目Application下进行sdk的初始化工作
```
HekrSDK.init(getApplicationContext(), R.raw.config);
```

## 二、用户接口

###2.1、当前用户token
#### 示例code
```
import me.hekr.hummingbird.action.HekrUser;
import me.hekr.hummingbird.action.HekrUserAction;

private HekrUserAction hekrUserAction;
  
hekrUserAction = HekrUserAction.getInstance(context);
//用户token
hekrUserAction.getJWT_TOKEN();
//用户唯一ID
hekrUserAction.getUserId();

```
###2.2、用户登录
#### 请求参数
|key |类型及范围 |说明|
|:--|:--|:--|
|userName|String|用户名|
|passWord|String|用户密码|
#### 返回结果
```
{
    "access_token": "xxx",
    "refresh_token": "xxx",
    "token_type": "bearer",
    "expires_in": 86399,
    "jti": "7ee5ade2-3d6b-4581-93b6-fea526337742"
}
```
#### 示例code
```
import me.hekr.hummingbird.action.HekrUser;
import me.hekr.hummingbird.action.HekrUserAction;

private HekrUserAction hekrUserAction;
  
hekrUserAction = HekrUserAction.getInstance(context);

hekrUserAction.login(userName, passWord, new HekrUser.LoginListener() {
    @Override
    public void loginSuccess(String str) {
            //登录成功
        }
    @Override
    public void loginFail(int errorCode) {
            //登录失败            
        }
    });
```
###2.3、第三方登录
注意: 若要使用第三方登录，必须先在各大平台中申请第三方登录权限，申请通过后将key值填写至config.json中,根据1.1中的说明将第三方的Activity在AndroidManifest.xml中填写完整!
示例code
```
 qq_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HekrOAuthLoginActivity.class);
                    //第二个参数为第三方类型
                intent.putExtra(HekrOAuthLoginActivity.OAUTH_TYPE, HekrUserAction.OAUTH_QQ);
                    //第二个参数为第三方类型
                startActivityForResult(intent, HekrUserAction.OAUTH_QQ);
            }
        });
        
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String certificate = data.getStringExtra(HekrOAuthLoginActivity.OAUTH_CODE);
            if (!TextUtils.isEmpty(certificate)) {
                switch (requestCode) {
                    case HekrUserAction.OAUTH_QQ:
                        //移动端OAuth接口
                        break;
                    case HekrUserAction.OAUTH_WECHAT:
                        //移动端OAuth接口
                        break;
                    case HekrUserAction.OAUTH_SINA:
                        //移动端OAuth接口
                        break;
                }
            }
        }
    }
```
|第三方类型 |说明|
|:--|:--|
|HekrUserAction.OAUTH_QQ|QQ| 
|HekrUserAction.OAUTH_WECHAT|微信| 
|HekrUserAction.OAUTH_SINA|微博| 
注意：[3.13文档][3.13]中微博登录需要uid参数，sdk中已直接将uid参数封装至获取到certificate中，所以微博登录时可省略掉uid参数。或者直接使用SDK中的hekrUserAction.OAuthLogin();
```
http://uaa.openapi.hekr.me/MOAuth?type=SINA&pid=0000000000&clientType=ANDROID&certificate=certificate
```
SDK示例Code
```
//通过上一步拿到的certificate进行第三方登录
hekrUserAction.OAuthLogin(HekrUserAction.OAUTH_SINA, String certificate, new HekrUser.MOAuthListener() {
    @Override
    public void mOAuthSuccess(MOAuthBean moAuthBean) {
        //该OAuth账号还未和主账号绑定
        }

    @Override
    public void mOAuthSuccess(JWTBean jwtBean) {
         //该OAuth账号已经和主账号绑定
        }

    @Override
   public void mOAuthFail(int errorCode) {
        //失败
        }
    });
```
## 三、设备配网
配网步骤说明：1、发现设备 2、调用云端绑定接口进行绑定

###3.1、发现设备：[发现设备示例代码][31]
###3.2、绑定云端接口
##### 示例code
```
import me.hekr.hummingbird.bean.FindDeviceBean;
import me.hekr.hummingbird.action.HekrUserAction;
import me.hekr.hummingbird.bean.DeviceBean;
import me.hekr.hummingbird.bean.DeviceStatusBean;

private HekrUserAction hekrUserAction;

hekrUserAction = HekrUserAction.getInstance(this);

//在第一步发现设备callBackDevice回调函数中调用以下示例代码进行设备绑定
hekrUserAction.deviceBindStatusAndBind(findDeviceBean.getDevTid(), findDeviceBean.getBindKey(), new HekrUser.GetBindStatusAndBindListener() {
        @Override
        public void getStatusSuccess(List<DeviceStatusBean> deviceStatusBeanLists) {
            //获取局域网内设备状态成功                
        }
        @Override
        public void getStatusFail(int errorCode) {
            //获取局域网内设备状态失败                    
        }
        @Override
        public void bindDeviceSuccess(DeviceBean deviceBean) {
            //绑定局域网内设备成功                     
        }
        @Override
        public void bindDeviceFail(int errorCode) {
            //绑定局域网内设备失败                    
        }
    });
```

## 四、设备控制
控制先决条件：用户登录成功

###4.1、发送控制命令
#### 请求参数
|key |类型及范围 |说明|
|:--|:--|:--|
|object|Object|web控制页对象,若控制页为Android Native编写可直接当前activity的引用 例：MainActivity.this|
|devTid|String|设备tid|
|protocol|JSONObject|指令协议 [参考协议][42] 该协议中包含的msgID和appTid字段将由SDK自动补全|
|dataReceiverListener|HekrData.dataReceiverListener|命令发送回调|

#### 回调返回值
```
{
  "msgId" : 291,
  "action" : "appSendResp",
  "code" : 200,
  "desc" : "success",
  "params" : {
    "devTid" : "xxxxxxx",
    "ctrlKey" : "xxxxxxxxxx",
    "appTid" : "xxxxxxxx",
    "data" : {
      "raw" : "48xxxxx"
    }
  }
}
```
#### 示例code
```
import me.hekr.hummingbird.util.MsgUtil
import me.hekr.hummingbird.action.HekrData;

String command={
  'action' : 'appSend',
  'params' : {
    'devTid' :'devTid',                 
    'ctrlKey' :'xxxxxx',             
    'data' : {
      'raw':'48xxxxxxx'
    }
  }
}
MsgUtil.sendMsg(TemplateActivity.this, tid, new JSONObject(command), new HekrData.dataReceiverListener() {
        @Override
        public void onReceiveSuccess(String msg) {
            //接收返回命令                        
        }

        @Override
        public void onReceiveTimeout() {
            //命令接收超时                       
        }
    });
```
###4.2、主动接收设备上报控制命令
#### 请求参数
|key |类型及范围 |说明|
|:--|:--|:--|
|object|Object|web控制页面对象,若控制页为Android Native编写可直接当前activity的引用 例：MainActivity.this,额外说明：标识对象 当该对象释放后将不再接收消息|
|filter|JSONObject|过滤条件 如果某个key的值为NULL表示只检查该key是否存在|
|dataReceiverListener|HekrData.dataReceiverListener|命令接收回调接口|

#### 回调函数
```
void onReceiveSuccess(String msg);
void onReceiveTimeout();
```
|params |说明|
|:--|:--|:--|
|msg|符合条件的协议 [参考协议][42]|

#### 示例code
```
import me.hekr.hummingbird.util.MsgUtil
import me.hekr.hummingbird.action.HekrData;

String filter={
    'action' : 'devSend',
    'params' : {
    'devTid' : 'xxxxxxx'
    }
}

MsgUtil.receiveMsg(TemplateActivity.this, new JSONObject(filter), new HekrData.dataReceiverListener() {
        @Override
        public void onReceiveSuccess(String msg) {
            //设备主动上报命令                                
        }

        @Override
        public void onReceiveTimeout() {
           //暂无用处 
        }
    });
```
## 五、云端接口 
###5.1 openAPI中 [认证授权API][51]和[用户API][52]中的接口访问包括http get、post、delete、put、patch操作，请直接使用hekrUserAction操作，hekrUserAction可自动管理token，此类中封装了大量的常见接口。
获取设备列表 示例code
```
import me.hekr.hummingbird.action.HekrUser;
import me.hekr.hummingbird.action.HekrUserAction;

private HekrUserAction hekrUserAction;
  
hekrUserAction = HekrUserAction.getInstance(context);

hekrUserAction.getDevices(new HekrUser.GetDevicesListener() {
        @Override
        public void getDevicesSuccess(List<DeviceBean> devicesLists) {
            //获取设备列表成功
        }

        @Override
        public void getDevicesFail(int errorCode) {
            //获取设备列表失败
        }
    });
```
|返回参数 |说明|
|:--|:--|
|devicesLists|设备列表|
|errorCode|错误码|
###5.2 其他未封装的接口操作请直接使用
```
getHekrData()
postHekrData()
putHekrData()
deleteHekrData()
patchHekrData()
//上传文件 uri为文件路径
uploadFile(String uri)
```
示例code (get/post,其他方法类似)
```
 hekrUserAction.getHekrData(url, new HekrUserAction.GetHekrDataListener() {
        @Override
        public void getSuccess(Object object) {
            //get成功
        }

        @Override
        public void getFail(int errorCode) {
            //get失败
            String errorMsg = HekrCodeUtil.errorCode2Msg(errorCode);
        }
    });
hekrUserAction.postHekrData(url, entity, new HekrUserAction.GetHekrDataListener() {
        @Override
        public void getSuccess(Object object) {
            //post成功
        }

        @Override
        public void getFail(int errorCode) {
            //post失败
            String errorMsg = HekrCodeUtil.errorCode2Msg(errorCode);
        }
    });
```
## 六、错误码使用说明
Hekr SDK中可以直接通过errorCode获取到对应的errorMessage
```
String errorMsg = HekrCodeUtil.errorCode2Msg(errorCode);
```
|errorCode |说明|
|:--|:--|
|0|网络超时|
|1|Token过期,需要重新登录|
|2|未知错误|

其他具体错误码参见API文档
[认证授权错误码][53]
[用户API错误码][54]
[企业API错误码][55]

[1]:https://developer.android.com/studio/index.html
[0]:http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[2]:https://services.gradle.org/distributions
[3]:https://github.com/HEKR-Cloud/HEKR-ANDROID-SDK/tree/3.0
[11]:https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=1417751808&token=&lang=zh_CN
[31]:https://github.com/HEKR-Cloud/HEKR-ANDROID-SDK/blob/3.0/ConfigDemo/app/src/main/java/com/example/hekr/configdemo/ConfigActivity.java#L75-L120
[3.13]:http://docs.hekr.me/v4/developerGuide/openapi/#313-oauth
[42]: http://www.hekr.me/docsv4/resourceDownload/protocol/json/
[51]:http://docs.hekr.me/v4/developerGuide/openapi/#3-api
[52]:http://docs.hekr.me/v4/developerGuide/openapi/#4-api
[53]:http://docs.hekr.me/v4/developerGuide/openapi/#_10
[54]:http://docs.hekr.me/v4/developerGuide/openapi/#_17
[55]:http://docs.hekr.me/v4/developerGuide/openapi/#_20
