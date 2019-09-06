package com.example.bookkeeping.entity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Bill extends LitePalSupport {
    private Integer id;
    //消费金额
    private Double amount;
    private Integer expenditureId;
    private Integer payMethodId;
    //备注
    private String remark;
    //消费时间
    private String consumptionTime;

    public Expenditure getExpenditure() {
        if(expenditureId!=null){
            return LitePal.find (Expenditure.class,expenditureId);
        }
        return null;
    }

    public PayMethod getPayMethod() {
        if(payMethodId!=null){
            return LitePal.find (PayMethod.class,payMethodId);
        }
        return null;
    }
}
