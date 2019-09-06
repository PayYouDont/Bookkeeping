package com.example.bookkeeping.entity;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
/**
* @Author peiyongdong
* @Description ( 支付方式 )
* @Date 15:59 2019/9/2
* @Param
* @return
**/
public class PayMethod extends LitePalSupport {
    private Integer id;
    private String name;
}
