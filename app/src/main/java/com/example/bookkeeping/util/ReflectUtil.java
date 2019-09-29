package com.example.bookkeeping.util;

import com.example.bookkeeping.entity.AppVersion;

import org.json.JSONObject;
import org.litepal.util.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ezy.boost.update.UpdateInfo;

public class ReflectUtil {
    public String createTableSqlByClass(Class clazz){

        return null;
    }
    public static List<Field> getFields(Class<?> clazz) {
        //属性集合
        List<Field> fieldList = new ArrayList<> ();
        //获取实体类属性
        Field[] fields = clazz.getDeclaredFields();
        fieldList = addFildToList(fieldList, fields);
        //获取父类属性
        if(clazz.getSuperclass() instanceof Class) {
            Field[] supfields = clazz.getSuperclass().getDeclaredFields();
            fieldList = addFildToList(fieldList, supfields);
        }
        return fieldList;
    }
    public static List<Field> addFildToList(List<Field> fieldList,Field[] fields){
        for(int i=0;i<fields.length;i++) {
            Field field = fields[i];
            field.setAccessible(true);
            fieldList.add(field);
        }
        return fieldList;
    }
    public static Class<?> getGeneric(Collection<?> collection){
        if(collection.size()>0) {
            return collection.iterator().next().getClass();
        }
        return null;
    }
    public static List<Field> getFields(Collection<?> collection) {
        return getFields(getGeneric(collection));
    }
    public static AppVersion parseToAppversion(String versionJson){
        AppVersion appVersion = null;
        if(versionJson!=null){
            try {
                JSONObject object = new JSONObject (versionJson);
                appVersion = new AppVersion ();
                appVersion.setVersionCode (object.getInt ("versionCode"));
                appVersion.setVersionName (object.getString ("versionName"));
                appVersion.setUpdateLog (object.getString ("updateLog"));
                appVersion.setForcedUpdate (object.getBoolean ("forcedUpdate"));
                appVersion.setApkUrl (object.getString ("apkUrl"));
                appVersion.setApkSize (object.getDouble ("apkSize"));
                appVersion.setMd5 (object.getString ("md5"));
            }catch (Exception e){
                LogUtil.e ("AppVersion",e);
            }
        }
        return appVersion;
    }
    public static UpdateInfo parseToUpdateInfo(String versionJson){
        AppVersion appVersion = parseToAppversion (versionJson);
        UpdateInfo updateInfo = null;
        if(appVersion!=null){
            updateInfo = new UpdateInfo ();
            updateInfo.isForce = appVersion.isForcedUpdate ();
            updateInfo.versionCode = appVersion.getVersionCode ();
            updateInfo.versionName = appVersion.getVersionName ();
            updateInfo.updateContent = appVersion.getUpdateLog ();
            updateInfo.md5 = appVersion.getMd5 ();
            updateInfo.size = new Long (new Double (appVersion.getApkSize ()*1024*1024).longValue ());
            updateInfo.url = appVersion.getApkUrl ();
        }
        return updateInfo;
    }

}
