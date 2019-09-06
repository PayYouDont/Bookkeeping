package com.example.bookkeeping.chart;

import android.graphics.Typeface;
import android.view.View;

import com.example.bookkeeping.entity.Bill;

import java.util.List;

public abstract class BaseChartData{
    private static final int PERMISSION_STORAGE = 0;

    protected Typeface tfRegular;
    protected Typeface tfLight;
    protected List<Bill> billList;
    protected View root;
    public BaseChartData(List<Bill> billList, View root) {
        this.billList = billList;
        this.root = root;
        tfRegular = Typeface.createFromAsset(root.getResources ().getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(root.getResources ().getAssets(), "OpenSans-Light.ttf");
    }
}
