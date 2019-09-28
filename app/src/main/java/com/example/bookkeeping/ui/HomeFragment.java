package com.example.bookkeeping.ui;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookkeeping.R;
import com.example.bookkeeping.chart.MyProgressBar;
import com.example.bookkeeping.chart.PieChartData;
import com.example.bookkeeping.datepicker.CustomDatePicker;
import com.example.bookkeeping.datepicker.DateFormatUtils;
import com.example.bookkeeping.entity.BaseFragment;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.ProgressData;
import com.example.bookkeeping.util.StringUtil;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private View root;
    private ProgressData progressData;
    private PieChart pieChart;
    private MyProgressBar progressBar;
    private EditText startDateText,endDateText,expectedDateText,totalText;
    private CustomDatePicker startTimerPicker,endTimerPicker,expectedPicker;
    private LinearLayout hideLayout,slidLayout;
    private FloatingActionButton billAddBtn;
    private RadioGroup radioGroup;
    private Double currentAmount = 0d;
    private List<Integer> groupIds;
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        progressData = LitePal.find (ProgressData.class,1);
        root = inflater.inflate (R.layout.fragment_home, container, false);
        //进度条数据结束
        totalText = root.findViewById (R.id.home_total_text);
        totalText.setOnEditorActionListener ((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //在这个方法中处理你需要处理的事件
                saveData ();
                return true;
            }
            return false;
        });
        //timePicker
        startDateText = root.findViewById (R.id.home_start_date);
        startDateText.setOnClickListener (v ->  startTimerPicker.show(startDateText.getText().toString()));
        endDateText = root.findViewById (R.id.home_end_date);
        endDateText.setOnClickListener (v ->  endTimerPicker.show(endDateText.getText().toString()));
        expectedDateText = root.findViewById (R.id.home_expected_date);
        expectedDateText.setOnClickListener (v ->  expectedPicker.show(expectedDateText.getText().toString()));
        //chart
        pieChart = root.findViewById (R.id.home_chart_pie);
        progressBar = root.findViewById (R.id.home_progressBar);
        init();
        //radio
        radioGroup = root.findViewById (R.id.home_radio_group);
        radioGroup.setOnCheckedChangeListener ((group, checkedId) -> showChart(checkedId));
        if(progressData!=null&&groupIds.contains (progressData.getRadioGroupCheckedId ())){
            radioGroup.check (progressData.getRadioGroupCheckedId ());
        }else{
            radioGroup.check (R.id.home_radio_customize);
        }
        billAddBtn = root.findViewById (R.id.bill_add_btn);
        billAddBtn.setOnClickListener (v -> {
            FragmentTransaction transaction = getFragmentManager ().beginTransaction ();
            Fragment fragment = new EditFragment (new Bill ());
            transaction.replace (R.id.nav_host_fragment,fragment).commit ();
        });
        hideLayout = root.findViewById (R.id.hide_layout);
        slidLayout = root.findViewById (R.id.slid_layout);
        slidLayout.setOnTouchListener ((v, event) -> slidding (event));
        setRootHeight (root);
        return root;
    }
    //初始化相关数据
    private void init(){
        TextView balanceText = root.findViewById (R.id.home_balance_text);
        TextView balanceNumberText = root.findViewById (R.id.home_balance_number);
        TextView spendText = root.findViewById (R.id.home_spend_text);
        TextView spendNumberText = root.findViewById (R.id.home_spend_number);
        progressBar.setBalanceText (balanceText);
        progressBar.setBalanceNumberText (balanceNumberText);
        progressBar.setSpendText (spendText);
        progressBar.setSpendNumberText (spendNumberText);
        groupIds = new ArrayList<> ();
        groupIds.add (R.id.home_radio_year);
        groupIds.add (R.id.home_radio_month);
        groupIds.add (R.id.home_radio_day);
        groupIds.add (R.id.home_radio_customize);
    }
    //选择时间后展示数据
    private void showChart(int checkId){
        Date endDate = new Date ();
        if(progressData!=null&&!StringUtil.isEmpty (progressData.getExpectedDate ())){
            Date date = DateFormatUtils.parse (progressData.getExpectedDate ());
            if(!checkDateIsOut (date,endDate)){
                endDate = date;
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime (endDate);
        Date startDate = new Date ();
        if(progressData==null||radioGroup.getCheckedRadioButtonId ()!=R.id.home_radio_customize
                || StringUtil.isEmpty (progressData.getStartDate ())){
            switch (checkId){
                case R.id.home_radio_year:
                    calendar.add (Calendar.YEAR,-1);
                    startDate = calendar.getTime ();
                    break;
                case R.id.home_radio_customize:
                case R.id.home_radio_month:
                    calendar.add (Calendar.MONTH,-1);
                    startDate = calendar.getTime ();
                    break;
                case R.id.home_radio_day:
                    calendar.add (Calendar.DAY_OF_MONTH,-1);
                    startDate = calendar.getTime ();
            }
        }else{
            startDate = DateFormatUtils.parse (progressData.getStartDate ());
            if(progressData.getTotal ()!=null){
                totalText.setText (progressData.getTotal ().toString ());
            }
        }
        String startDateStr = DateFormatUtils.long2Str(startDate.getTime (), true);
        String endDateStr = DateFormatUtils.long2Str(endDate.getTime (), true);
        initTimerPicker (startDateStr,endDateStr);

    }
    //初始化日历
    private void initTimerPicker(String startDate,String endDate) {
        String beginTime = "2000-01-01 00:00";
        String endTime = "2050-12-31 23:59";
        //起始时间
        startDateText.setText(startDate);
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        startTimerPicker = new CustomDatePicker (root.getContext (),(timestamp) -> {
            String startDateStr = DateFormatUtils.long2Str (timestamp, true);
            startDateText.setText (startDateStr);
            String endDateStr = endDateText.getText ().toString ();
            loadData (startDateStr,endDateStr);
        }, beginTime, endDate);
        //结束时间
        endDateText.setText(endDate);
        endTimerPicker = new CustomDatePicker (root.getContext (),(timestamp) -> {
            String startDateStr = startDateText.getText ().toString ();
            String endDateStr = DateFormatUtils.long2Str (timestamp, true);
            endDateText.setText (endDateStr);
            loadData (startDateStr,endDateStr);
        }, startDate, endTime);
        //预期时间
        if(progressData!=null&&!StringUtil.isEmpty (progressData.getExpectedDate ())){
            expectedDateText.setText (progressData.getExpectedDate ());
        }else{
            expectedDateText.setText (endDateText.getText ().toString ());
        }
        expectedPicker =  new CustomDatePicker (root.getContext (),(timestamp) -> {
            String expectedDate = (DateFormatUtils.long2Str (timestamp, true));
            String endDateStr = endDateText.getText ().toString ();
            if(!checkDateIsOut (expectedDate,endDateStr)){
                Toast.makeText (root.getContext (),"预期时间不能小于当前时间",Toast.LENGTH_SHORT).show ();
                return;
            }
            expectedDateText.setText (expectedDate);
            saveData ();
        }, beginTime, endTime);
        loadData (startDate,endDate);
    }
    public void loadData(String startTime,String endTime){
        currentAmount = 0d;
        String whereSql = "datetime(consumptionTime) between datetime('"+startTime+"') and datetime('"+endTime+"')";
        List<Bill> billList = LitePal.where (whereSql).find (Bill.class);
        billList.forEach (bill -> currentAmount += bill.getAmount ());
        new PieChartData (pieChart,root,billList);
        saveData();
    }
    //保存数据
    private void saveData(){
        String totalStr = totalText.getText ().toString ();
        if(StringUtil.isEmpty (totalStr)){
            return;
        }
        Double total = Double.parseDouble (totalStr);
        String expectedDate = expectedDateText.getText ().toString ();
        if(StringUtil.isEmpty (expectedDate)){
            Toast.makeText (root.getContext (),"预期总金额和预期时间不能为空！",Toast.LENGTH_SHORT).show ();
            return;
        }
        progressData = new ProgressData ();
        progressData.setTotal (total);
        progressData.setExpectedDate (expectedDate);
        progressData.setId (1);
        progressData.setRadioGroupCheckedId (radioGroup.getCheckedRadioButtonId ());
        progressData.setStartDate (startDateText.getText ().toString ());
        progressData.setEndDate (endDateText.getText ().toString ());
        progressData.saveOrUpdate ("id=1");
        updateProgress(progressData);
    }
    //更新进度条
    private void updateProgress(ProgressData data){
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
        progressBar.setExpectedCount ((total/expectedDayCount)*currentDayCount);//预计花费
        progressBar.setRealCount (currentAmount);//实际花费
        if(total==0||expectedDayCount==0){
            Toast.makeText (root.getContext (),"预期金额不能为0且预期时间应大于起始时间",Toast.LENGTH_SHORT).show ();
        }else{
            int porgress = (int)(progressBar.getRealCount ()*100/progressBar.getExpectedCount ());
            progressBar.setProgress (porgress);
            totalText.setText (total.toString ());
        }
    }

    private boolean checkDateIsOut(String expectedDate,String endDate){
        return checkDateIsOut(DateFormatUtils.parse (expectedDate),DateFormatUtils.parse (endDate));
    }
    private boolean checkDateIsOut(Date expectedDate,Date endDate){
        return expectedDate.getTime ()-endDate.getTime ()>0;
    }
    float mPosY=0,mCurPosY=0;
    private boolean slidding(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurPosY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(mCurPosY - mPosY > 0&& (Math.abs(mCurPosY - mPosY) > 25)) {
                    //向下滑
                    hideLayout.setVisibility (View.VISIBLE);
                    slidLayout.getChildAt (0).setRotation (180);
                }else if(mCurPosY - mPosY < 0&& (Math.abs(mCurPosY - mPosY) > 25)){
                    hideLayout.setVisibility (View.GONE);
                    slidLayout.getChildAt (0).setRotation (0);
                }
                break;
        }
        return true;
    }
}