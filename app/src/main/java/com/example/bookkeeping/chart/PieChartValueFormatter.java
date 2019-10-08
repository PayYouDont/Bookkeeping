package com.example.bookkeeping.chart;

import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class PieChartValueFormatter extends ValueFormatter {

    public DecimalFormat mFormat;
    public PieChartValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        return pieEntry.getLabel ()+":"+super.getPieLabel (value, pieEntry);
    }
}
