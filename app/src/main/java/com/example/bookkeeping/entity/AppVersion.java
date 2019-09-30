package com.example.bookkeeping.entity;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName AppVersion
 * @Description TODO
 * @Author pay
 * @DATE 2019/9/20 17:01
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class AppVersion extends LitePalSupport {
    private Integer id = 1;
    private Integer versionCode;
    private String versionName;
    private String updateLog;
    private boolean forcedUpdate;
    private boolean ignorable;
    private String apkUrl;
    private Long apkSize;
    private String md5;
}
