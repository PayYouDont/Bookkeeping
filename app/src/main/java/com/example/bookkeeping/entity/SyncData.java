package com.example.bookkeeping.entity;

import com.example.bookkeeping.util.ReflectUtil;
import com.example.bookkeeping.util.StringUtil;

import lombok.Data;

@Data
public class SyncData {
    //0:count 1:ProgressData 2:Bill
    private int type;
    private String requestData;
    private String responseData;
    private int count;
    private int progress;
    public ProgressData getProgressData(){
        if (type == 1&& StringUtil.isEmpty (responseData)){
            return ReflectUtil.parseToProgressData (responseData);
        }
        return null;
    }
    public Bill getBillData(){
        if (type == 2&& StringUtil.isEmpty (responseData)){
            return ReflectUtil.parseToBillData (responseData);
        }
        return null;
    }
}
