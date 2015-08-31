package com.hekr.android.app.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.hekr.android.app.model.TemplatesInfo;
import com.hekr.android.app.model.TemplatesValue;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xubukan on 2015/3/29.
 */
public class CacheHelper {

    public static void doUpdateProductIconList(){
        ProductIconUpdateRunnable productIconUpdateRunnable = new ProductIconUpdateRunnable();
        //new Thread(productIconUpdateRunnable).start();
        ThreadPool threadPool = ThreadPool.getThreadPool();
        threadPool.addTask(productIconUpdateRunnable);


//      ProviderIconUpdateRunnable providerIconUpdateRunnable = new ProviderIconUpdateRunnable();
//      new Thread(providerIconUpdateRunnable).start();
    }
    public static void doUpdateProductHtmlList(Context context){
        ProductHtmlUpdateRunnable productHtmlUpdateRunnable = new ProductHtmlUpdateRunnable(context);
        ThreadPool threadPool = ThreadPool.getThreadPool();
        threadPool.addTask(productHtmlUpdateRunnable);
        //new Thread(productHtmlUpdateRunnable).start();
    }
}
//设备图片
class ProductIconUpdateRunnable implements Runnable{
    @Override
    public void run() {
        String productIconList = HttpHelper.getProductIconList();
        //Log.d("MyLog","刚启动时从服务器检查产品有无更新---------"+productIconList);

        //Log.d("MyLog",productIconList);
        if(productIconList!=null&&productIconList!="")
        {
            //http://user.hekr.me/res/api/categories.json返回的json将categoryCB(和);去掉变成 productIconList
            productIconList = productIconList.replace("categoryCB(","").replace(");", "");

            try{
                JSONObject json = new JSONObject(productIconList);
                //Log.d("MyLog","遍历过后的json:"+json);
                Iterator it = json.keys();
                //Log.d("MyLog","--------------------mmmmmmmmmm------");

                while (it.hasNext()){
                    //id为"1"类推的字符串取他的值
                    String id = it.next().toString();

                    //Log.d("MyLog","遍历过后的所有id:"+id);

                    JSONObject iconInfo = json.optJSONObject(id);
                    //Log.d("MyLog","服务器上的产品："+iconInfo);
                    //里面有id,name,logo_url,和updated_at
                    String updated = iconInfo.optString("updated_at");
                    String name = iconInfo.optString("name");
                    String ename = iconInfo.optString("ename");
                    //Log.d("MyLog","服务器产品name："+name+"--"+"服务器更新时间:"+updated);

                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                    SQLiteDatabase db = mg.getDatabase("db");
                    if(mg.needProductIconInsert(id)){
                        //更新图片缓存
                        //再次切开
                        JSONObject logoUrlJson = iconInfo.optJSONObject("logo_url");
                        //Log.d("MyLog","logoUrlJson："+logoUrlJson);
                        //还是一个JSONObject，normal再次切出来就是"normal":"/images/logo/categories/icon_01@3x.png"
                        String normalLogoUrl = logoUrlJson.getString("normal");
                        if(normalLogoUrl!=null){
                            Log.d("MyLog","需要增加的网络图片路径："+normalLogoUrl);
                        }
                        String imageURL=normalLogoUrl;
                        String realPath="/mnt/sdcard/Hekr/"+imageURL.substring(imageURL.lastIndexOf("/") + 1);
                        if(realPath!=null)
                        {
                            Log.d("MyLog",realPath);
                        }
                        //变成了"/images/logo/categories/icon_01@3x.png"
                        if(!("".equals(normalLogoUrl))&&normalLogoUrl!=null){
                            AsyncBitmapLoader.SaveBitmap(imageURL);
                            db.execSQL("insert into category(id,name,ename,logo_url,updated_at) values(?,?,?,?,?)",
                                    new String[]{id,name,ename,realPath,updated});
                        }
                    }
                    if(mg.needProductIconUpdate(id, updated))
                    {
                        //更新图片缓存
                        //再次切开
                        JSONObject logoUrlJson = iconInfo.optJSONObject("logo_url");
                        //还是一个JSONObject，normal再次切出来就是"normal":"/images/logo/categories/icon_01@3x.png"
                        String normalLogoUrl = logoUrlJson.getString("normal");
                        if(normalLogoUrl!=null){
                            Log.d("MyLog","需要更新的网络图片路径："+normalLogoUrl);
                        }
                        String imageURL=normalLogoUrl;
                        String realPath="";
                        if(!("".equals(imageURL))&&imageURL!=null){
                            realPath="/mnt/sdcard/Hekr/"+imageURL.substring(imageURL.lastIndexOf("/") + 1);
                        }

                        if(realPath!=null)
                        {
                            Log.d("MyLog",realPath);
                        }
                        //变成了"/images/logo/categories/icon_01@3x.png"
                        if(!("".equals(imageURL))&&imageURL!=null){
                            AsyncBitmapLoader.SaveBitmap(imageURL);
                            db.execSQL("update category set id=?,updated_at=?,logo_url=? where id=?",
                                    new String[]{id,updated,realPath,id});
                        }
                    }
                    Cursor cursor=null;
                    try {
                        cursor = db.rawQuery("select id,name,ename,logo_url,updated_at from category where id=?",
                                new String[]{id});
                        if (cursor.moveToNext()) {
                            //Log.d("MyLog", "修改后的id:"+cursor.getString(0)+"修改后的name:"+cursor.getString(1)+"修改后的ename:"+cursor.getString(2)+"修改后的图片路径:" + cursor.getString(3)+"修改后的时间："+cursor.getString(4));
                        } else {
                            Log.d("MyLog", "查询数据库路径失败");
                        }
                    }catch (Exception e){
                        Log.d("MyLog","异常：查询数据:"+e.getMessage());
                    }finally {
                        if(cursor!=null) {
                            cursor.close();
                        }
                    }
                }
            }catch (Exception e){
                Log.d("MyLog", "缓存图片抛异常:"+e.toString());
            }

        }
    }
}

class ProviderIconUpdateRunnable implements Runnable{
    @Override
    public void run() {
        String providerIconList = HttpHelper.getProviderIconList();

        if(providerIconList!=null&&providerIconList!=""){
            //http://user.hekr.me/res/api/categories.json返回的json将categoryCB(和);去掉变成 productIconList
            providerIconList = providerIconList.replace("providerCB(","").replace(");","");

            try{
                JSONObject json = new JSONObject(providerIconList);

                Iterator it = json.keys();
                while (it.hasNext()){
                    //id为"1"类推的字符串取他的值
                    String id = it.next().toString();

                    JSONObject iconInfo = json.getJSONObject(id);
                    //里面有id,name,logo_url,和updated_at
                    String updated = iconInfo.getString("updated_at");

                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();

                    if(mg.needproviderIconUpdate(id, updated)){
                        //更新图片缓存
                        //再次切开
                        JSONObject logoUrlJson = iconInfo.getJSONObject("logo_url");
                        //还是一个JSONObject，normal再次切出来就是"normal":"/images/logo/categories/icon_01@3x.png"
                        String normalLogoUrl = logoUrlJson.getString("normal");
                        //变成了"/images/logo/categories/icon_01@3x.png"
                        AsyncBitmapLoader.SaveBitmap(iconInfo.getString(normalLogoUrl));
                        //TODO

                    }
                }
            }catch (Exception e){

            }

        }
    }
}

class ProductHtmlUpdateRunnable implements Runnable{

    private Context context;

    public ProductHtmlUpdateRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        String pageInformationList = HttpHelper.getUpdateTestJson();
        List<TemplatesValue> infoData=new ArrayList<TemplatesValue>();
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        SQLiteDatabase db = mg.getDatabase("db");
        int code=401;
        String message="error";

        Log.d("MyLog","pageInformationList:"+pageInformationList);
        if(!"".equals(pageInformationList)&&pageInformationList!=null)
        {
//            FileUtils fileUtils =new FileUtils();
//            File resultFile= fileUtils.write2SDFromInput("HekrHtml/","localhtml.rar",HttpHelper.getHtml("http://nbct01.baidupcs.com/file/e9f51b67d2251f9124f1c5f798afca5d?bkt=p2-nb-929&fid=203316868-250528-248308592355659&time=1439963841&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-Cjgvy3fFa6sPC0udL%2B61ST0f%2FDc%3D&to=nbhb&fm=Nin,B,T,t&sta_dx=0&sta_cs=0&sta_ft=rar&sta_ct=1&fm2=Ningbo,B,T,t&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400e9f51b67d2251f9124f1c5f798afca5dd58da82e000000056819&sl=76808271&expires=8h&rt=sh&r=828648895&mlogid=2321328063&vuk=-&vbdid=145173792&fin=localhtml.rar&fn=localhtml.rar&slt=pm&uta=0&rtype=1&iv=0&isw=0"));
//
//            if(fileUtils.isFileExist("HekrHtml/localhtml.rar"))
//            {
//                Log.d("MyLog","结果:"+"已有文件");
//                try {
//                    fileUtils.unzipFiles(resultFile,"/mnt/sdcard/HekrHtml/1/");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }else{
//                if(resultFile==null){
//                    Log.d("MyLog","结果:"+"失败");
//                }
//                else{
//                    Log.d("MyLog","结果:"+"成功");
//                }
//            }
//                try {
//                    if (Environment.getExternalStorageState().equals(
//                            Environment.MEDIA_MOUNTED))
//                    {
//                        String sdpath = Environment.getExternalStorageDirectory() + "/";
//                        Log.d("MyLog", "sdpath" + sdpath);
//                        HttpHelper.saveZip("http://nb.poms.baidupcs.com/file/e9f51b67d2251f9124f1c5f798afca5d?bkt=p2-nb-929&fid=203316868-250528-248308592355659&time=1439879213&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-YYq5jy%2BT5WSgLnXTKnyXO3ioyKE%3D&to=nbb&fm=Nin,B,T,ny&sta_dx=0&sta_cs=0&sta_ft=rar&sta_ct=0&fm2=Ningbo,B,T,ny&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=1400e9f51b67d2251f9124f1c5f798afca5dd58da82e000000056819&sl=78381135&expires=8h&rt=sh&r=306235461&mlogid=18218329&vuk=-&vbdid=145173792&fin=localhtml.rar&fn=localhtml.rar&slt=pm&uta=0&rtype=1&iv=0&isw=0"
//                                , "/mnt/sdcard/HekrHtml/");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            //TemplatesInfo info= com.alibaba.fastjson.JSON.parseObject(pageInformationList, TemplatesInfo.class);
            //Log.i("info","info:"+info);
            try {
                JSONObject info = new JSONObject(pageInformationList);
                code=info.getInt("code");
                message=info.getString("message");
                Log.i("MyLog","code:"+code+"message:"+message);
                String value=info.getString("values");
                Log.i("MyLog","value:"+value);
                if(value!=null)
                {
                    try {
                        JSONArray value_list = new JSONArray(value);
                        for(int i=0;i<value_list.length();i++){
                            JSONObject item = value_list.getJSONObject(i);
                            TemplatesValue templatesValue=new TemplatesValue();
                            if(item.has("op")){
                                templatesValue.setOp(item.getString("op"));
                            }
                            if(item.has("name")){
                                templatesValue.setName(item.getString("name"));
                            }
                            if(item.has("version")){
                                templatesValue.setVersion(item.getString("version"));
                            }
                            if(item.has("hash")){
                                templatesValue.setHash(item.getString("hash"));
                            }
                            if(item.has("url")){
                                templatesValue.setUrl(item.getString("url"));
                            }
                            infoData.add(templatesValue);
                        }
                        Log.i("MyLog","infodata:"+infoData);

                    } catch (JSONException e) {
                        Log.i("UpdateHtml","value_list解析成templatesValue对象出错");
                        e.printStackTrace();
                    }
                }
                else{
                    Log.i("UpdateHtml","TemplatesValue:"+value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else{
            Log.i("UpdateHtml","pageInformationList为空");
        }
        if(code==200&&"ok".equals(message)){
            for(int i=0;i<infoData.size();i++){
                if(!"".equals(infoData.get(i).getUrl())&&infoData.get(i).getUrl()!=null){
                    if("update".equals(infoData.get(i).getOp())){

                    }
                    if("add".equals(infoData.get(i).getOp())){

                    }
                    if("remove".equals(infoData.get(i).getOp())){

                    }
                    downloadAllAssets("http://nb.poms.baidupcs.com/file/4d8f7daf766c6e7a79e6044d7d35dc89?bkt=p2-nb-929&fid=203316868-250528-546803092925760&time=1440724406&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-f9CmexU53M2ack2gHk1LyP7AELw%3D&to=nbb&fm=Nin,B,U,ny&sta_dx=0&sta_cs=0&sta_ft=zip&sta_ct=3&fm2=Ningbo,B,U,ny&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=14004d8f7daf766c6e7a79e6044d7d35dc89311267cc0000000573d4&sl=71237711&expires=8h&rt=sh&r=965407036&mlogid=3265722509&vuk=-&vbdid=3150609193&fin=localhtml.zip&fn=localhtml.zip&slt=pm&uta=0&rtype=1&iv=0&isw=0",context);
                    long time=System.currentTimeMillis();
                    Log.i("MyLog","nowTime:"+time);

                    SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String xztime=sdf.format(new Date(time));
                    Log.i("MyLog","nowTime:"+xztime);



                    String now=String.format("%tR",time);
                    Log.i("MyLog","nowTime:"+now);

                    Cursor cursor=null;
                    try {
                        cursor=db.rawQuery("select count(*) from timemanage",null);
                        if (cursor.getCount()==0) {
                            db.execSQL("insert into timemanage(nowtime) values(?)", new String[]{xztime});
                        }
                        else{
                            db.execSQL("update timemanage set nowtime =?", new String[]{xztime});
                        }

                        cursor = db.rawQuery("select * from timemanage", null);
                        if (cursor.moveToNext()) {
                            Log.d("MyLog", "nowtime:"+cursor.getString(0));
                        } else {
                            Log.d("MyLog", "查询数据库路径失败");
                        }
                    }catch (Exception e){
                        Log.d("MyLog","异常：查询数据:"+e.getMessage());
                    }finally {
                        if(cursor!=null) {
                            cursor.close();
                        }
                    }
                }
            }
        }
        else{
            Log.i("UpdateHtml","code!=200||message!='ok'");
        }

    }
    private static void downloadAllAssets( String url,Context context) {
        // Temp folder for holding asset during download
        File zipDir =  ExternalStorage.getSDCacheDir(context, "tmp");
        // File path to store .zip file before unzipping
        File zipFile = new File( zipDir.getPath() + "/temp.zip" );
        // Folder to hold unzipped output
        File outputDir = ExternalStorage.getSDCacheDir( context, "htmlTemplate" );

        try {
            DownloadFile.download( url, zipFile, zipDir );
            unzipFile( zipFile, outputDir );
        } finally {
            zipFile.delete();
        }
    }
    protected static void unzipFile( File zipFile, File destination ) {
        DecompressZip decomp = new DecompressZip( zipFile.getPath(),
                destination.getPath() + File.separator );
        decomp.unzip();
    }
}
