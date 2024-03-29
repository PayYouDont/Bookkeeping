package com.example.bookkeeping.chart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;

import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.Bill;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class PieChartData implements OnChartValueSelectedListener {
    private PieChart chart;
    private BarChart barChart;
    private List<Bill> billList;
    private View root;
    private Map<String,List<Bill>> pieDataMap;
    public PieChartData(PieChart chart, View root,List<Bill> billList) {
        this.chart = chart;
        barChart = root.findViewById (R.id.home_chart_bar);
        //chart.setUsePercentValues(true);//按百分比显示
        chart.getDescription().setEnabled(false);
        // 和四周相隔一段距离,显示数据
        chart.setExtraOffsets(5, 5, 5, 5);
        chart.setEntryLabelColor (Color.BLACK);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterText(generateCenterSpannableText());
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        //是否绘制标签
        chart.setDrawEntryLabels (false);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        //是否绘制PieChart内部中心文本
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
        this.billList = billList;
        this.root = root;
        setData ();
    }
    private void setData() {

        ArrayList<PieEntry> entries = new ArrayList<>();
        Map<String,Float> map = new HashMap<> ();
        pieDataMap = new HashMap<> ();
        billList.forEach (bill -> {
            String key = bill.getExpenditure ().getName ();
            Float value = Float.valueOf (bill.getAmount ().toString ());
            List<Bill> bills= new ArrayList<> ();
            if(map.containsKey (key)){
                value += map.get (key);
                bills = pieDataMap.get (key);
            }
            bills.add (bill);
            map.put (key,value);
            pieDataMap.put (key,bills);
        });
        map.forEach ((label,amount)->{
            entries.add(new PieEntry((amount), label));
        });
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        int[] MATERIAL_COLORS = {
                Color.rgb(200, 172, 255)
        };
        for (int c : MATERIAL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        //数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.8f);
        //设置连接线的颜色
        dataSet.setValueLineColor(Color.LTGRAY);
        dataSet.setValueLineVariableLength (true);
        dataSet.setUsingSliceColorAsValueLineColor(true);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PieChartValueFormatter ());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        //data.setValueTypeface(tf);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        // 更新 piechart 视图
        chart.invalidate();
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null){
            return;
        }
        PieEntry pieEntry = (PieEntry ) e;
        String label = pieEntry.getLabel ();
        List<Bill> bills = pieDataMap.get (label);
        new BarChartData (barChart,root,bills);
        barChart.setVisibility (View.VISIBLE);
    }

    @Override
    public void onNothingSelected() {
        barChart.setVisibility (View.GONE);
    }
    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("支出分布");
        s.setSpan(new RelativeSizeSpan (1f), 0, 4, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 4, s.length(), 0);
        return s;
    }
}
