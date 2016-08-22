# HEKR 3.0 SDK Android开发指南
> * 使用SDK开发之前请务必通读本文档
> * [Hekr SDK Demo](http://gitlab.hekr.me/jin123d/HekrSDKDemo)
> * [Hekr SDK Docs](http://jin123d.coding.me/HekrSDKDemo)
> * [氦氪云 OPEN API 调试页面](http://user.openapi.hekr.me/swagger-ui.html#!/)  

## 集成准备
* [Java JDK] [0]
* [Android SDK/Android Stuido][1]

### 1、下载SDK配置文件

* [SDK_CONFIG_FILES](https://raw.githubusercontent.com/HEKR-Cloud/HEKR-ANDROID-SDK/3.0/Hekr_SDK_Config_Android.zip)

### 2、快速导入SDK

* Please ensure that you are using the latest version by [ ![Download](https://api.bintray.com/packages/smartmatrix2014/maven/hekrSDK/images/download.svg) ](https://bintray.com/smartmatrix2014/maven/hekrSDK/_latestVersion)

* Gradle:
```
    compile 'me.hekr.hekrsdk:hekrsdk:1.0.0'
```
* Maven:
```
<dependency>
  <groupId>me.hekr.hekrsdk</groupId>
  <artifactId>hekrsdk</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
* [Or download hekrSDK aar](https://jcenter.bintray.com/me/hekr/hekrsdk/hekrsdk/)

## 一、配置
* 说明：本SDK中已使用以下依赖，请勿重复配置！
```
android-async-http-1.4.9.jar
annotations-java5-15.0.jar
eventbus-3.0.0.jar
fastjson-1.1.52.android.jar
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

* 在项目res目录下创建raw目录,将下载包中的config.json复制进去，config.json为项目的配置文件和第三方登录配置文件（填写各大平台申请的参数）。
* config.json文件配置说明：文件格式不可变，pid为在[氦氪console平台](http://console.hekr.me) 注册开发者后在个人中心->认证信息中相应的企业pid,配置文件中其他第三方登录数据在各大第三方平台申请填写，如不需要使用某些第三方登录则在相应位置留空即可。
* 如果需要第三方微信登录,则必须将下载包中的wxapi文件夹复制项目包名目录（微信开放平台填写的包名）下！【具体参考[微信开放平台文档][11]】
 
### 1.1、设置AndroidManifest.xml声明使用权限和服务
```

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
### 1.2、在项目Application下进行sdk的初始化工作
```
HekrSDK.init(getApplicationContext(), R.raw.config);
//打开log,默认为false
HekrSDK.openLog(true);
```

## 二、用户接口
氦氪用户接口包括了用户的注册、登录等部分。开发者需要先通过1.1和1.2正确初始化SDK后进行操作。


### 2.1、用户注册

用户注册分为手机号码注册和邮箱注册。

**手机号注册 流程示例code**
```
//1.获取图形验证码
hekrUserAction.getImgCaptcha()
//2.校验图形验证码
hekrUserAction.checkCaptcha()
//3.发送短信验证码
hekrUserAction.getVerifyCode()
//4.校验短信验证码
hekrUserAction.checkVerifyCode()
//5.使用手机号注册
hekrUserAction.registerByPhone()
```
**邮箱注册 示例code**
```
//使用邮箱注册
hekrUserAction.registerByEmail()
```
### 2.2、用户登录
```
hekrUserAction.login(String userName,String passWord,HekrUser.LoginListener loginListener);
```
**参数** 

|key |类型及范围 |说明|
|:--|:--|:--|
|userName|String|用户名|
|passWord|String|用户密码|

**返回结果**
```
{
    "access_token": "xxx",
    "refresh_token": "xxx",
    "token_type": "bearer",
    "expires_in": 86399,
    "jti": "7ee5ade2-3d6b-4581-93b6-fea526337742"
}
```
**示例code**
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
### 2.3、第三方登录
注意: 若要使用第三方登录，必须先在各大平台中申请第三方登录权限，申请通过后将key值填写至config.json中,根据1.1中的说明将第三方的Activity在AndroidManifest.xml中填写完整!

**示例code**
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
                        //之后可通过 氦氪openapi文档中3.13 或者 3.17 进行操作
                        break;
                    case HekrUserAction.OAUTH_WECHAT:
                        //之后可通过 氦氪openapi文档中3.13 或者 3.17 进行操作
                        break;
                    case HekrUserAction.OAUTH_SINA:
                        //之后可通过 氦氪openapi文档中3.13 或者 3.17 进行操作
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

**SDK示例Code**
```
//通过上一步拿到的certificate进行第三方登录
hekrUserAction.OAuthLogin(HekrUserAction.OAUTH_SINA,certificate, new HekrUser.MOAuthListener() {
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
### 2.4、当前用户token
**示例code（调用此接口前用户必须登录成功）**
```
//用户token
hekrUserAction.getJWT_TOKEN();
//用户唯一ID
hekrUserAction.getUserId();
```

## 三、设备配网
配网说明：仅适用于氦氪固件4.1.11.1及以上版本

### 3.1、开始配网

操作说明：请让模块处于配网模式
```
/**
* @param ssid wifi名称
* @param password wifi密码
* @param number 单次配网总时间
*/
smartConfig.startConfig(ssid, pwd, number);

```

### 3.2、配网时间内接收新设备信息

```
//接收配网设备信息
smartConfig.setNewDeviceListener(new SmartConfig.NewDeviceListener() {

            //单次配网时间内查询到的所有新设备(回调每次查询到的新设备)
            @Override
            public void getDeviceList(List<NewDeviceBean> newDeviceList) {

            }

            //单次配网时间内查询到的新设备(一旦有新的设备就会触发该回调接口)
            //只有newDeviceBean中属性bindResultCode值为0才算真正将该设备绑定到了自己账号下
            @Override
            public void getNewDevice(NewDeviceBean newDeviceBean) {
                
            }

            //单次配网时间内查到新设备
            @Override
            public void getDeviceSuccess() {

            }

            //单次配网时间内未查询到任何新设备
            @Override
            public void getDeviceFail() {

            }
        });

```
### 3.3、主动停止配网
注：(如不主动停止,将会在开始配网设置的时间内结束配网)
```
smartConfig.stopConfig();
```

### 3.4、补充说明

配网模式：    wifi模块在间隔2秒闪烁表示进入配网模式

新设备定义：  App配网过程中，模块处于以下情况App查询到的设备

             1、模块处于配网模式，成功绑定自己本次配网所用账号的设备，判定依据NewDeviceBean 属性bindResultCode值为0

             2、模块处于配网模式，但已被别人绑定的设备，判定依据NewDeviceBean 属性bindResultCode值为1 属性bindResultMsg值为E001：xxx，xxx即为真正绑定者的账号信息

             3、模块处于配网模式，但模块为非自行厂家模块，判定依据NewDeviceBean 属性bindResultCode值为1 属性bindResultMsg值为E003

             4、模块处于配网模式，已被自己账号绑定上并未删除解绑的设备，判定依据NewDeviceBean 属性bindResultCode值为1 属性bindResultMsg值为E004

**示例code**

[示例配网地址][34]

```
import me.hekr.hekrsdk.bean.NewDeviceBean;
import me.hekr.hekrsdk.util.SmartConfig;

Activity可以替换为自己的Activity
smartConfig = new SmartConfig(Activity.this);

/**
* 开始配网
* @param ssid wifi名称
* @param password wifi密码
* @param number 单次配网总时间
*/
smartConfig.startConfig(ssid, pwd, number);

//接收配网设备信息
smartConfig.setNewDeviceListener(new SmartConfig.NewDeviceListener() {

            //单次配网时间内，查询到的所有新设备(回调每次查询到的新设备列表)
            @Override
            public void getDeviceList(List<NewDeviceBean> newDeviceList) {

            }

            //单次配网时间内，查询到的新设备(一旦有新的设备就会触发该回调接口)
            //只有newDeviceBean中属性bindResultCode值为0才算真正将该设备绑定到了自己账号下
            @Override
            public void getNewDevice(NewDeviceBean newDeviceBean) {
                
            }

            //单次配网时间内，查到新设备
            @Override
            public void getDeviceSuccess() {

            }

            //单次配网时间内，未查询到新设备
            @Override
            public void getDeviceFail() {

            }
        });

//主动停止配网
smartConfig.stopConfig();
```

## 四、设备控制
控制先决条件：用户登录成功

### 4.1、发送控制命令
**请求参数**

|key |类型及范围 |说明|
|:--|:--|:--|
|object|Object|web控制页对象,若控制页为Android Native编写可直接当前activity的引用 例：MainActivity.this|
|devTid|String|设备tid|
|protocol|JSONObject|指令协议 [参考协议][42] 该协议中包含的msgID和appTid字段将由SDK自动补全|
|dataReceiverListener|DataReceiverListener|命令发送回调|
|isAutoPassageway|boolean|isAutoPassageway为false 只使用云端通道发送控制命令，isAutoPassageway为true 当前局域网内有设备时优先使用局域网通道发送控制命令(3秒未回复使用云端通道发送)局域网无设备直接使用云端通道发送控制命令|

**回调返回值**
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
**示例code**
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
MsgUtil.sendMsg(TemplateActivity.this, tid, new JSONObject(command), new DataReceiverListener() {
        @Override
        public void onReceiveSuccess(String msg) {
            //接收返回命令                        
        }

        @Override
        public void onReceiveTimeout() {
            //命令接收超时                       
        }
    },false);
```
### 4.2、主动接收设备上报控制命令
**请求参数**
|key |类型及范围 |说明|
|:--|:--|:--|
|object|Object|web控制页面对象,若控制页为Android Native编写可直接当前activity的引用 例：MainActivity.this,额外说明：标识对象 当该对象释放后将不再接收消息|
|filter|JSONObject|过滤条件 如果某个key的值为NULL表示只检查该key是否存在|
|dataReceiverListener|DataReceiverListener|命令接收回调接口|

**回调函数**
```
void onReceiveSuccess(String msg);
void onReceiveTimeout();
```
|params |说明|
|:--|:--|:--|
|msg|符合条件的协议 [参考协议][42]|

**示例code**
```
import me.hekr.hummingbird.util.MsgUtil
import me.hekr.hummingbird.action.HekrData;

String filter={
    'action' : 'devSend',
    'params' : {
    'devTid' : 'xxxxxxx'
    }
}

MsgUtil.receiveMsg(TemplateActivity.this, new JSONObject(filter), new DataReceiverListener() {
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

### 4.3、云端返回所有协议信息

作用：接收云端所有协议信息(例如appResp、devSend、appLoginResp等等动作信息)，便于后续自行开发处理。

**示例code**
```
    import android.content.BroadcastReceiver;
    import android.content.Intent;
    import android.content.IntentFilter;

    public class DeviceCtrlActivity extends Activity{

        private MsgBroadReceiver msgBroadReceiver;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_xxx);
            createBroadcast();
        }

        /**
        * 创建广播接收器，用来接收SDK中发出的云端协议信息
        */
        private void createBroadcast() {
            msgBroadReceiver = new MsgBroadReceiver();
            IntentFilter filter = new IntentFilter();
            //只有持有相同的action的接受者才能接收此广播
            filter.addAction(ConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
            registerReceiver(msgBroadReceiver, filter);
        }
        class MsgBroadReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                //云端返回所有信息
                String backData=intent.getStringExtra(ConstantsUtil.HEKR_WS_PAYLOAD);
                Log.i("broadReceiver", backData);
            }
        }
        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (msgBroadReceiver != null) {
                unregisterReceiver(msgBroadReceiver);
            }
        }
    }

```

## 五、云端接口 
### 5.1 openAPI中 [认证授权API][51]和[用户API][52]中的接口访问包括http get、post、delete、put、patch操作，请直接使用hekrUserAction操作，hekrUserAction可自动管理token，此类中封装了大量的常见接口。
**示例code**
```
import me.hekr.hummingbird.action.HekrUser;
import me.hekr.hummingbird.action.HekrUserAction;

private HekrUserAction hekrUserAction;
  
hekrUserAction = HekrUserAction.getInstance(context);
//3.18获取图形验证码
hekrUserAction.getImgCaptcha()
//3.19校验图形验证码
hekrUserAction.checkCaptcha()
//3.1 发送短信验证码
hekrUserAction.getVerifyCode()
//3.2 校验短信验证码
hekrUserAction.checkVerifyCode()
//3.3 使用手机号注册用户
hekrUserAction.registerByPhone()
//3.4 使用邮箱注册用户
hekrUserAction.registerByEmail()
//3.5 用户登录
hekrUserAction.login()
//3.6 重置密码
hekrUserAction.resetPwd()
//3.7 修改密码
hekrUserAction.changePassword()
//3.8 修改用户手机号
hekrUserAction.changePhoneNumber()
//3.9 发送重置密码邮件
hekrUserAction.sendResetPwdEmail()
//3.10 重新发送确认邮件
hekrUserAction.reSendVerifiedEmail()
//3.11 发送修改邮箱邮件
hekrUserAction.sendChangeEmailStep1Email()
//3.12 刷新Access Token
hekrUserAction.refresh_token()
//3.13 移动端OAuth
hekrUserAction.OAuthLogin()
//3.14 将OAuth账号和主账号绑定
hekrUserAction.bindOAuth()
//3.15 解除OAuth账号和主账号的绑定关系
hekrUserAction.unbindOAuth()
//3.16 移动端使用微信第三方账号登录
hekrUserAction.weChatMOAuth()
//3.17 创建匿名Hekr主账户并与当前登录三方账户绑定
hekrUserAction.createUserAndBind()
//4.1.1 绑定设备
hekrUserAction.bindDevice()
//4.1.2 列举设备列表
hekrUserAction.getDevices()
//4.1.3 删除设备
hekrUserAction.deleteDevice()
//4.1.4 更改设备名称/描述
hekrUserAction.renameDevice()
//4.1.5 获取当前局域网内所有设备绑定状态
hekrUserAction.deviceBindStatus()
//4.1.8 查询设备属主
hekrUserAction.queryOwner()
//4.2.1 添加目录
hekrUserAction.addFolder()
//4.2.2 列举目录
hekrUserAction.getFolder()
//4.2.3 修改目录名称
hekrUserAction.renameFolder()
//4.2.4 删除目录
hekrUserAction.deleteFolder()
//4.2.5 将设备挪到指定目录
hekrUserAction.devicesPutFolder()
//4.2.6 将设备从目录挪到根目录下
hekrUserAction.folderToRoot()
//4.3.2 反向授权创建 -1.授权用户创建授权二维码
hekrUserAction.oAuthCreateCode()
//4.3.2 反向授权创建 -2.被授权用户扫描该二维码
hekrUserAction.registerAuth()
//4.3.2 反向授权创建 -3.授权用户收到被授权者的请求
hekrUserAction.getOAuthInfoRequest()
//4.3.2 反向授权创建 -4.授权用户同意
hekrUserAction.agreeOAuth()
//4.3.2 反向授权创建 -5.授权用户拒绝
hekrUserAction.refuseOAuth()
//4.3.4 取消授权
hekrUserAction.cancelOAuth()
//4.3.5 列举授权信息
hekrUserAction.getOAuthList()
//4.5.1 获取用户档案
hekrUserAction.getProfile()
//4.5.2 更新用户档案
hekrUserAction.setProfile()
//4.5.16 上传文件
hekrUserAction.uploadFile()
//4.5.19 绑定推送标签接口
hekrUserAction.pushTagBind()
//4.7.2 列举群组
hekrUserAction.getGroup()
//5.1 判断设备模块固件是否需要升级
hekrUserAction.checkFirmwareUpdate()
//5.2 根据pid获取企业资讯
hekrUserAction.getNewsByPid()
//5.5 售后管理 - 针对设备反馈问题
hekrUserAction.feedback()
//退出登录
hekrUserAction.userLogout()
//获取登录用户UID
hekrUserAction.getUserId()
//获取缓存到本地的用户档案
hekrUserAction.getUserCache()
```

**5.2 其他未封装的接口操作请直接使用**
```
hekrUserAction.getHekrData()
hekrUserAction.postHekrData()
hekrUserAction.putHekrData()
hekrUserAction.deleteHekrData()
hekrUserAction.patchHekrData()
//上传文件 uri为文件路径
hekrUserAction.uploadFile(String uri)
```
示例code (get/post,其他方法类似)
```
 private HekrUserAction hekrUserAction;
  
 hekrUserAction = HekrUserAction.getInstance(context);

 //get
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

 //post entity
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
[31]:https://coding.net/u/jin123d/p/HekrSDKDemo/git/blob/master/app/src/main/java/me/hekr/demo/ConfigActivity.java
[3.13]:http://docs.hekr.me/v4/reference/openapi/#313-oauth
[3.13]:http://docs.hekr.me/v4/reference/openapi/#317-hekr
[34]:http://gitlab.hekr.me/jin123d/HekrSDKDemo/blob/master/app/src/main/java/me/hekr/demo/ConfigActivity.java
[42]: http://www.hekr.me/docsv4/resourceDownload/protocol/json/
[51]:http://docs.hekr.me/v4/reference/openapi/#3-api
[52]:http://docs.hekr.me/v4/reference/openapi/#4-api
[53]:http://docs.hekr.me/v4/reference/openapi/#_10
[54]:http://docs.hekr.me/v4/reference/openapi/#_17
[55]:http://docs.hekr.me/v4/reference/openapi/#_20
