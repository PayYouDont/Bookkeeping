package com.example.bookkeeping.entity;

import com.example.bookkeeping.util.JsonUtil;
import com.example.bookkeeping.util.ReflectUtil;
import com.example.bookkeeping.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class SyncData {
    //0:开始 1:ProgressData 2:Bill 3:完成
    private int type;
    private String requestData;
    private String responseData;
    private int count;
    private int progress;
    public Map<String,Object> getCountData(){
        if (type == 0&&!StringUtil.isEmpty (responseData)){
            return ReflectUtil.parseToMap (responseData);
        }
        return null;
    }
    public ProgressData getProgressData(){
        if (type == 1&&!StringUtil.isEmpty (responseData)){
            return JsonUtil.toBean (responseData,ProgressData.class);
        }
        return null;
    }
    public Bill getBillData(){
        if (type == 2&&!StringUtil.isEmpty (responseData)){
            return JsonUtil.toBean (responseData,Bill.class);
        }
        return null;
    }
}
