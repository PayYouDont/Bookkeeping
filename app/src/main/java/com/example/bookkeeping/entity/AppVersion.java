package com.example.bookkeeping.entity;

import lombok.Data;

/**
 * @ClassName AppVersion
 * @Description TODO
 * @Author pay
 * @DATE 2019/9/20 17:01
 **/
@Data
public class AppVersion {
    private Integer versionCode;
    private String versionName;
    private String updateLog;
    private boolean forcedUpdate;
    private String apkUrl;
    private Double apkSize;
    private String md5;
}
