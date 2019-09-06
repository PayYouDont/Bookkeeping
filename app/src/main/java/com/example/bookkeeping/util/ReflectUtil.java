package com.example.bookkeeping.util;

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
    /*public static String createTableByClass(Class clazz){
        if (clazz==null){
            return null;
        }
        StringBuilder builder = new StringBuilder ();
        builder.append ("create table ");
        String tableName = getTableName (clazz);
        builder.append (tableName);
        builder.append ("( ");
        List<Field> fields = getFields (clazz);
        fields.forEach (field -> {
            Annotation IdAnnotation = field.getAnnotation (Id.class);
            GeneratedValue GVAnnotation = field.getAnnotation (GeneratedValue.class);
            String fieldName = field.getName ();
            String fieldType = getLastStrByPattern(field.getType ().getTypeName ());
            if(fieldType.equals ("double")){
                fieldType = "real";
            }else if (fieldType.equals ("boolean")){
                fieldType = "blob";
            }else if (fieldType.indexOf ("int")!=-1){
                fieldType = "integer";
            }else{
                fieldType = "text";
            }
            if(IdAnnotation!=null){
                builder.append (fieldName+" "+fieldType+" primary key");
                if(GVAnnotation!=null){
                    String strategy = GVAnnotation.strategy ().name ().toLowerCase ();
                    builder.append (" "+strategy+"increment");
                }
                builder.append (", ");
            }else{
                builder.append (fieldName+" "+fieldType+", ");
            }
        });
        builder.deleteCharAt (builder.lastIndexOf (","));
        builder.append (")");
        return builder.toString ();
    }*/
    public static String getTableName(Class clazz){
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
    }
}
