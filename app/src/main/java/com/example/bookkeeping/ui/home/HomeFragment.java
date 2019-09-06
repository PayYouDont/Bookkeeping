package com.example.bookkeeping.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookkeeping.R;
import com.example.bookkeeping.chart.MyProgressBar;
import com.example.bookkeeping.datepicker.CustomDatePicker;
import com.example.bookkeeping.datepicker.DateFormatUtils;
import com.example.bookkeeping.chart.BarChartData;
import com.example.bookkeeping.entity.BaseFragment;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.chart.PieChartData;
import com.example.bookkeeping.entity.ProgressData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private Double currentAmount = 0d;
    private View root;
    private PieChart pieChart;
    private BarChart barChart;
    private MyProgressBar progressBar;
    private EditText startDateText,endDateText,expectedDateText,totalText;
    private CustomDatePicker startTimerPicker,endTimerPicker,expectedPicker;
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.fragment_home, container, false);
        startDateText = root.findViewById (R.id.home_start_date);
        startDateText.setOnClickListener (v ->  startTimerPicker.show(startDateText.getText().toString()));
        endDateText = root.findViewById (R.id.home_end_date);
        endDateText.setOnClickListener (v ->  endTimerPicker.show(endDateText.getText().toString()));
        pieChart = root.findViewById (R.id.home_chart_pie);
        barChart = root.findViewById (R.id.home_chart_bar);
        progressBar = root.findViewById (R.id.home_progressBar);
        totalText = root.findViewById (R.id.home_total_text);
        expectedDateText = root.findViewById (R.id.home_expected_date);
        expectedDateText.setOnClickListener (v ->  expectedPicker.show(expectedDateText.getText().toString()));
        Button settingBtn = root.findViewById (R.id.home_total_setting);
        settingBtn.setOnClickListener (v -> settingPropress ());
        RadioGroup radioGroup = root.findViewById (R.id.home_radio_group);
        radioGroup.setOnCheckedChangeListener ((group, checkedId) -> showData(checkedId));
        radioGroup.check (R.id.home_radio_month);
        setRootHeight (root);
        return root;
    }
    private void settingPropress(){
        String totalStr = totalText.getText ().toString ();
        if(totalStr.trim ().equals ("")){
            return;
        }
        Double total = Double.parseDouble (totalStr);
        String expectedDate = expectedDateText.getText ().toString ();
        if(expectedDate.equals ("")){
            Toast.makeText (root.getContext (),"预期总金额和预期时间不能为空！",Toast.LENGTH_SHORT).show ();
            return;
        }
        ProgressData data = new ProgressData ();
        data.setTotal (total);
        data.setExpectedDate (expectedDate);
        data.setId (1);
        data.saveOrUpdate ("id=1");
        showProgress(data);
    }
    //更新进度条
    private void showProgress(ProgressData data){
        Double total = data.getTotal ();
        String expectedDate = data.getExpectedDate ();
        String startDate = startDateText.getText ().toString ();
        String endDate = endDateText.getText ().toString ();
        Long expected = DateFormatUtils.parse (expectedDate).getTime ();
        Long start = DateFormatUtils.parse (startDate).getTime ();
        Long end = DateFormatUtils.parse (endDate).getTime ();
        int dayMillis = 24*60*60*1000;
        int currentDayCount = (int)((end - start)/dayMillis);
        int expectedDayCount = (int)((expected - start)/dayMillis);
        if(total==0||expectedDayCount==0){
            Toast.makeText (root.getContext (),"预期金额不能为0且预期时间应大于起始时间",Toast.LENGTH_SHORT).show ();
        }else{
            //int porgress = (int)((currentAmount/total)*100/(currentDayCount/expectedDayCount));
            int porgress = (int)(currentAmount*100/((total/expectedDayCount)*currentDayCount));
            progressBar.setProgress (porgress);
            totalText.setText (total.toString ());
        }
    }
    //选择时间后展示数据
    private void showData(int checkId){
        Date startDate = new Date ();
        Date endDate = new Date ();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime (endDate);
        switch (checkId){
            case R.id.home_radio_year:
                calendar.add (Calendar.YEAR,-1);
                startDate = calendar.getTime ();
                break;
            case R.id.home_radio_month:
                calendar.add (Calendar.MONTH,-1);
                startDate = calendar.getTime ();
                break;
            case R.id.home_radio_day:
                calendar.add (Calendar.DAY_OF_MONTH,-1);
                startDate = calendar.getTime ();
        }
        String startTime = DateFormatUtils.long2Str(startDate.getTime (), true);
        String endTime = DateFormatUtils.long2Str(endDate.getTime (), true);
        initTimerPicker (startTime,endTime);
        ProgressData progressData = LitePal.find (ProgressData.class,1);
        if(progressData!=null){
            showProgress (progressData);
        }
    }
    //初始化日历
    private void initTimerPicker(String startDate,String endDate) {
        String beginTime = "2000-01-01 00:00";
        //String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = "2050-12-31 23:59";
        //String now = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        startDateText.setText(startDate);
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        startTimerPicker = new CustomDatePicker (root.getContext (),(timestamp) -> {
            String startDateStr = DateFormatUtils.long2Str (timestamp, true);
            startDateText.setText (startDateStr);
            String endDateStr = endDateText.getText ().toString ();
            loadData (startDateStr,endDateStr);
        }, beginTime, endDate);
        // 允许点击屏幕或物理返回键关闭
        startTimerPicker.setCancelable(true);
        // 显示时和分
        startTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        startTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        startTimerPicker.setCanShowAnim(true);
        endDateText.setText(endDate);
        endTimerPicker = new CustomDatePicker (root.getContext (),(timestamp) -> {
            String startDateStr = startDateText.getText ().toString ();
            String endDateStr = DateFormatUtils.long2Str (timestamp, true);
            endDateText.setText (endDateStr);
            loadData (startDateStr,endDateStr);
        }, startDate, endTime);
        // 允许点击屏幕或物理返回键关闭
        endTimerPicker.setCancelable(true);
        // 显示时和分
        endTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        endTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        endTimerPicker.setCanShowAnim(true);
        loadData (startDate,endDate);
        ProgressData progressData = LitePal.find (ProgressData.class,1);
        if(progressData!=null){
            String expectedDate = progressData.getExpectedDate ();
            if(expectedDate!=null&&!expectedDate.equals ("")){
                expectedDateText.setText (expectedDate);
            }else{
                expectedDateText.setText (endDateText.getText ().toString ());
            }
        }else{
            expectedDateText.setText (endDateText.getText ().toString ());
        }
        expectedPicker =  new CustomDatePicker (root.getContext (),(timestamp) -> expectedDateText.setText (DateFormatUtils.long2Str (timestamp, true)), beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        expectedPicker.setCancelable(true);
        // 显示时和分
        expectedPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        expectedPicker.setScrollLoop(true);
        // 允许滚动动画
        expectedPicker.setCanShowAnim(true);
    }
    public void loadData(String startTime,String endTime){
        String whereSql = "datetime(consumptionTime) between datetime('"+startTime+"') and datetime('"+endTime+"')";
        List<Bill> billList = LitePal.where (whereSql).find (Bill.class);
        billList.forEach (bill -> currentAmount += bill.getAmount ());
        new PieChartData (pieChart,root,billList);
        new BarChartData (barChart,root,billList);
    }
}