package com.example.bookkeeping.entity;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ProgressData extends LitePalSupport {
    private Integer id;
    private Double total;
    private String expectedDate;
    private String name;
    private Integer status;
    private Integer radioGroupCheckedId;
    private String startDate;
    private String endDate;
}
