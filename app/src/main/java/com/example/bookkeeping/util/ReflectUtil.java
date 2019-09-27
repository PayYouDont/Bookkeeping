package com.example.bookkeeping.util;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
   /* public static String getTableName(Class clazz){
        return getLastStrByPattern (clazz.getName ());
    }
    public static String getLastStrByPattern(String str,String pattern){
        if(str.indexOf (pattern)==-1){
            return str;
        }
        return str.substring (str.lastIndexOf (pattern)+1).toLowerCase ();
    }
    public static String getLastStrByPattern(String str){
        return getLastStrByPattern (str,".");
    }*/
}
