package com.example.bookkeeping.chart;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.MyValueFormatter;
import com.example.bookkeeping.entity.XYMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BarChartData extends BaseChartData implements OnChartValueSelectedListener{
    private BarChart chart;
    public BarChartData(BarChart chart, View root, List<Bill> billList) {
        super(billList,root);
        this.chart = chart;
        chart.setOnChartValueSelectedListener(this);
        //显示每条背景阴影
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        //不显示描述信息
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);
        chart.setPinchZoom(false);
        //是否显示网格背景
        chart.setDrawGridBackground(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter (new ValueFormatter () {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if(value<billList.size ()){
                    Bill bill = billList.get (index);
                    return bill.getExpenditure ().getName ();
                }
                return "";
            }
        });
        ValueFormatter custom = new MyValueFormatter ("￥");
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        Legend l = chart.getLegend();
        //显示位置
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        XYMarkerView mv = new XYMarkerView (root.getContext (), new ValueFormatter () {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                Bill bill = billList.get (index);
                return bill.getConsumptionTime ();
            }
        });
        mv.setBackgroundColor (Color.argb (125,135,135,135));
        mv.setChartView(chart); // For bounds control
        //mv.setLabelFor ();
        chart.setMarker(mv); // Set the marker to the chart
        setData();
    }
    private void setData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < billList.size (); i++) {
            Bill bill = billList.get (i);
            Float val = Float.valueOf (bill.getAmount ().toString ());
            values.add(new BarEntry(i, val));
        }
        String label = "支出分布";
        if(billList.size ()>0){
            label = billList.get (0).getExpenditure ().getName ();
        }
        BarDataSet set1;
        if (chart.getData() != null &&chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, label);
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            //是否显示顶部的值
            set1.setDrawValues(true);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            chart.setData(data);
            data.setValueTypeface(tfLight);
            chart.setFitBars(true);
        }

        chart.invalidate();
    }
    private final RectF onValueSelectedRectF = new RectF();
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;

        RectF bounds = onValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index","low: " + chart.getLowestVisibleX() + ", high: "+ chart.getHighestVisibleX());
        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {}

}
