package com.example.bookkeeping.util;

import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.ProgressData;
import com.example.bookkeeping.entity.SyncData;

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
                appVersion.setIgnorable (object.getBoolean ("ignorable"));
                appVersion.setVersionCode (object.getInt ("versionCode"));
                appVersion.setVersionName (object.getString ("versionName"));
                appVersion.setUpdateLog (object.getString ("updateLog"));
                appVersion.setForcedUpdate (object.getBoolean ("forcedUpdate"));
                appVersion.setApkUrl (object.getString ("apkUrl"));
                appVersion.setApkSize (object.getLong ("apkSize"));
                appVersion.setMd5 (object.getString ("md5"));
            }catch (Exception e){
                LogUtil.e ("ReflectUtil",e);
            }
        }
        return appVersion;
    }
    public static UpdateInfo parseToUpdateInfo(String versionJson){
        AppVersion appVersion = parseToAppversion (versionJson);
        UpdateInfo updateInfo = null;
        if(appVersion!=null){
            updateInfo = new UpdateInfo ();
            updateInfo.isIgnorable = appVersion.isIgnorable ();
            updateInfo.isForce = appVersion.isForcedUpdate ();
            updateInfo.versionCode = appVersion.getVersionCode ();
            updateInfo.versionName = appVersion.getVersionName ();
            updateInfo.updateContent = appVersion.getUpdateLog ();
            updateInfo.md5 = appVersion.getMd5 ();
            updateInfo.size = new Long (appVersion.getApkSize ());
            updateInfo.url = appVersion.getApkUrl ();
        }
        return updateInfo;
    }
    public static ProgressData parseToProgressData(String responseData){
        ProgressData progressData = null;
        if(responseData!=null){
            try {
                JSONObject object = new JSONObject (responseData);
                progressData = new ProgressData ();
                progressData.setId (object.getInt ("id"));
                progressData.setTotal (object.getDouble ("total"));
                progressData.setExpectedDate (object.getString ("expectedDate"));
                progressData.setName (object.getString ("name"));
                progressData.setStatus (object.getInt ("status"));
                progressData.setRadioGroupCheckedId (object.getInt ("radioGroupCheckedId"));
                progressData.setStartDate (object.getString ("startDate"));
                progressData.setEndDate (object.getString ("endDate"));
            }catch (Exception e){
                LogUtil.e ("AppVersion",e);
            }
        }
        return progressData;
    }
    public static Bill parseToBillData(String responseData){
        Bill billData = null;
        if(responseData!=null){
            try {
                JSONObject object = new JSONObject (responseData);
                billData = new Bill ();
                billData.setId (object.getInt ("id"));
                billData.setAmount (object.getDouble ("amount"));
                billData.setConsumptionTime (object.getString ("consumptionTime"));
                billData.setExpenditureId (object.getInt ("expenditureId"));
                billData.setPayMethodId (object.getInt ("methodId"));
                billData.setRemark (object.getString ("remark"));
            }catch (Exception e){
                LogUtil.e ("ReflectUtil",e);
            }
        }
        return billData;
    }
    public static SyncData parseToSyncData(String msg){
        SyncData syncData = null;
        if(msg!=null){
            try {
                JSONObject object = new JSONObject (msg);
                syncData = new SyncData ();
                Integer count = object.getInt ("count");
                if(count!=null){
                    syncData.setCount (count);
                }
                Integer progress = object.getInt ("progress");
                if(progress!=null){
                    syncData.setProgress (progress);
                }
                String requestData = object.getString ("requestData");
                if(!StringUtil.isEmpty (requestData)){
                    syncData.setRequestData (requestData);
                }
                String responseData = object.getString ("responseData");
                if(!StringUtil.isEmpty (responseData)){
                    syncData.setResponseData (responseData);
                }
                syncData.setType (object.getInt ("type"));
            }catch (Exception e){
                LogUtil.e ("ReflectUtil",e);
            }
        }
        return syncData;
    }
    public static String toJson(Object o){
        JSONObject object = new JSONObject ();
        List<Field> fieldList = getFields (o.getClass ());
        fieldList.forEach (field -> {
            try {
                object.put (field.getName (),field.get (o));
            }catch (Exception e){
                e.printStackTrace ();
            }
        });
        return object.toString ();
    }
}
