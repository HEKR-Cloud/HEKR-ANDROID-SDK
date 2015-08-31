package com.hekr.android.app.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by xubukan on 2015/3/24.
 */
public class MySettingsHelper {
    private static String setting_user = "";
    public static String getCookieUser(){
        if(setting_user!=null&&setting_user!=""){
            return setting_user;
        }else
        {
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
            SQLiteDatabase db = mg.getDatabase("db");
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("select setting_value from settings where setting_key=?",
                        new String[]{"user_credential"});
                cursor.moveToNext();
                return cursor.getString(0);
            }catch (Exception E){
                return null;
            }
            finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
        }

    }
}
