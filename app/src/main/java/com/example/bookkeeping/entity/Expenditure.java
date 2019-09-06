package com.example.bookkeeping.entity;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @Author peiyongdong
* @Description ( 消费 )
* @Date 16:08 2019/8/30
* @Param
* @return
**/
@Data
@EqualsAndHashCode(callSuper=false)
public class Expenditure extends LitePalSupport {
    private Integer id;
    //消费名称
    private String name;
    //图标id
    private Integer imageId;
    //状态(备用)
    private Integer status;
}
