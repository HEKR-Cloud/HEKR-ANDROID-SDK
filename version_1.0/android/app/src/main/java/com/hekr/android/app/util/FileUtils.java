package com.hekr.android.app.util;

import android.os.Environment;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;
/**
 * Created by xiaomao on 2015/8/17.
 * desc:
 */
public class FileUtils {
    private String SDPATH;

    public String getSDPATH() {
        return SDPATH;
    }
    public FileUtils() {
        //得到当前外部存储设备的目录
        // /SDCARD
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            //SDPATH = Environment.getExternalStorageDirectory() + "/";
            SDPATH="/mnt/sdcard/";
        }
    }
    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH+fileName);
        if(!file.exists()) {
            Log.d("MyLog", "creatSDFile()中file不存在并且重新重新创建！！！");
            try {
                file.createNewFile();
            }
            catch(IOException e){
                Log.d("MyLog", "createNewFile()异常："+e.getMessage());
                e.printStackTrace();
            }
            Log.d("MyLog","file绝对路径:"+file.getCanonicalPath().trim().toString());
        }
        if(file.exists()){
            try {
                Log.d("MyLog","file绝对路径:"+file.getCanonicalPath().trim().toString());
            } catch (IOException e) {
                Log.d("MyLog", "读取file路径抛出异常！！！");
            }
        }
        else{
            Log.d("MyLog","file路径不存在!!!");
        }
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        try{
        Log.d("MyLog", "未进行mkdirs()之前dir绝对路径:" + dir.getCanonicalPath().trim().toString());
            Log.d("MyLog", "未进行mkdirs()之前dir的abs绝对路径:" + dir.getAbsolutePath().trim().toString());
        }catch(IOException e){
            Log.d("MyLog", "未进行mkdirs()之前读取dir路径抛出异常！！！");
        }

        if(!dir.exists())
            dir.mkdirs();

        //测试
        if(dir.exists()){
            try {
                Log.d("MyLog","dir绝对路径:"+dir.getCanonicalPath().trim().toString());
                Log.d("MyLog","dir的abs绝对路径:"+dir.getAbsolutePath().trim().toString());
            } catch (IOException e) {
                Log.d("MyLog", "读取dir路径抛出异常！！！");
            }
        }
        else{
            Log.d("MyLog","dir路径不存在!!!");
        }
        return dir;
    }

    public void unzipFiles(File file,String destDir)
            throws FileNotFoundException,IOException {
        //压缩文件
        File srcZipFile=file;
        //基本目录
        if(!destDir.endsWith("/")){
            destDir+="/";
        }
        String prefixion=destDir;

        //压缩输入流
        ZipInputStream zipInput=new ZipInputStream(new FileInputStream(srcZipFile));
        //压缩文件入口
        ZipEntry currentZipEntry=null;
        //循环获取压缩文件及目录
        while((currentZipEntry=zipInput.getNextEntry())!=null){
            //获取文件名或文件夹名
            String fileName=currentZipEntry.getName();
            //Log.v("filename",fileName);
            //构成File对象
            File tempFile=new File(prefixion+fileName);
            //父目录是否存在
            if(!tempFile.getParentFile().exists()){
                //不存在就建立此目录
                tempFile.getParentFile().mkdir();
            }
            //如果是目录，文件名的末尾应该有“/"
            if(currentZipEntry.isDirectory()){
                //如果此目录不在，就建立目录。
                if(!tempFile.exists()){
                    tempFile.mkdir();
                }
                //是目录，就不需要进行后续操作，返回到下一次循环即可。
                continue;
            }
            //如果是文件
            if(!tempFile.exists()){
                //不存在就重新建立此文件。当文件不存在的时候，不建立文件就无法解压缩。
                tempFile.createNewFile();
            }
            //输出解压的文件
            FileOutputStream tempOutputStream=new FileOutputStream(tempFile);

            //获取压缩文件的数据
            byte[] buffer=new byte[8*1024];
            int hasRead=0;
            //循环读取文件数据
            while((hasRead=zipInput.read(buffer))>0){
                tempOutputStream.write(buffer,0,hasRead);
            }
            tempOutputStream.flush();
            tempOutputStream.close();
        }
        zipInput.close();
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName){
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    //删除文件
    public void delFile(String fileName){
        File file = new File(SDPATH + fileName);
        if(file.isFile()){
            file.delete();
        }
        file.exists();
    }
    //删除文件夹和文件夹里面的文件
    public void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    //File resultFile= fileUtils.write2SDFromInput("HekrHtml/","localhtml.rar",HttpHelper.getHtml(""))
    public File write2SDFromInput(String path,String fileName,InputStream input){
        FileOutputStream output = null;

        creatSDDir(path);
        try {
            File file = creatSDFile(path+fileName);
            if(file.exists()){
                try {
                    Log.d("MyLog","file绝对路径:"+file.getCanonicalPath().trim().toString());
                } catch (IOException e) {
                    Log.d("MyLog", "读取file路径抛出异常！！！");
                }
            }
            else{
                Log.d("MyLog","write2SDFromInput()中file路径不存在!!!");
            }
            if(!file.exists()){
                try {
                    file.createNewFile();
                }
                catch(IOException e){
                    Log.d("MyLog", "write2SDFromInput()中createNewFile()异常："+e.getMessage());
                    e.printStackTrace();
                }
                Log.d("MyLog","file绝对路径:"+file.getCanonicalPath().trim().toString());
            }
            output = new FileOutputStream(file);
            byte buffer [] = new byte[1024];
            while((input.read(buffer)) != -1){
                output.write(buffer);
            }
            output.flush();
            output.close();
            input.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

}

