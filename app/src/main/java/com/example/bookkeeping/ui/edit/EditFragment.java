package com.example.bookkeeping.ui.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bookkeeping.R;
import com.example.bookkeeping.datepicker.CustomDatePicker;
import com.example.bookkeeping.datepicker.DateFormatUtils;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.Expenditure;
import com.example.bookkeeping.entity.PayMethod;
import com.example.bookkeeping.entity.SpinnerData;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class EditFragment extends Fragment implements View.OnClickListener{
    private TextView mTvSelectedTime,dateText, consumeTypeSpinner;
    private CustomDatePicker mTimerPicker;
    private View root;
    //消费类型下拉控件,支付方式下拉控件
    private Spinner payMethodSpinner;
    @Setter
    public static Bill bill;
    //金额输入框
    private EditText consumeText,remarkText;
    private ImageView consumeImageView;
    private Bill billInstance;
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.fragment_edit, container, false);
        root.findViewById(R.id.ll_time).setOnClickListener(this);
        //消费类型下拉
        consumeTypeSpinner = root.findViewById (R.id.consume_type_spinner);
        consumeImageView = root.findViewById (R.id.consume_type_img);
        mTvSelectedTime = root.findViewById(R.id.tv_selected_time);
        //保存
        Button saveBtn = root.findViewById (R.id.edit_save_btn);
        saveBtn.setOnClickListener (v -> saveBill());
        consumeText = root.findViewById (R.id.consume_count);
        dateText = root.findViewById (R.id.tv_selected_time);
        remarkText = root.findViewById (R.id.remark);
        billInstance = new Bill ();
        if(bill!=null){
            billInstance.setId (bill.getId ());
            billInstance.setRemark (bill.getRemark ());
            billInstance.setConsumptionTime (bill.getConsumptionTime ());
            billInstance.setPayMethodId (bill.getPayMethodId ());
            billInstance.setExpenditureId (bill.getExpenditureId ());
            billInstance.setAmount (bill.getAmount ());
            consumeText.setText (billInstance.getAmount ().toString ());
            dateText.setText (billInstance.getConsumptionTime ());
            remarkText.setText (billInstance.getRemark ());
        }
        initTimerPicker();
        initSpinner ();
        bill = null;
        return root;
    }
    @Override
    public void onClick(View v) {
        // 日期格式为yyyy-MM-dd HH:mm
        mTimerPicker.show(mTvSelectedTime.getText().toString());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerPicker.onDestroy();
    }
    private void initTimerPicker() {
        String beginTime = "1990-01-01 00:00";
        //String endTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = "2099-12-31 23:59";
        String now = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        if(billInstance.getConsumptionTime ()==null){
            billInstance.setConsumptionTime (now);
        }
        mTvSelectedTime.setText(billInstance.getConsumptionTime ());
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker (root.getContext (),
                (timestamp) -> mTvSelectedTime.setText (DateFormatUtils.long2Str (timestamp, true)),
                beginTime, endTime);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }
    //保存账单
    private void saveBill(){
        String amount = consumeText.getText ().toString ();
        if(amount==null||amount.trim ().equals ("")){
            Toast.makeText (root.getContext (),"请输入消费金额！",Toast.LENGTH_LONG).show ();
            return;
        }
        PayMethod payMethod = LitePal.find (PayMethod.class,Integer.valueOf (SpinnerData.getSpinnerSelVal (payMethodSpinner)));
        String consumptionTime = dateText.getText ().toString ();
        String remark = remarkText.getText ().toString ();
        billInstance.setAmount (Double.valueOf (amount));
        billInstance.setPayMethodId (payMethod.getId ());
        billInstance.setConsumptionTime (consumptionTime);
        billInstance.setRemark (remark);
        if(billInstance.getId ()!=null){
            billInstance.saveOrUpdate ("id="+billInstance.getId ());
        }else{
            billInstance.save ();
        }
        View view = root.getRootView ().findViewById (R.id.navigation_history);
        view.performClick();
    }
    private void initSpinner(){
        List<Expenditure> expenditures = LitePal.findAll (Expenditure.class);
        if(billInstance.getExpenditureId ()==null){
           billInstance.setExpenditureId (expenditures.get (0).getId ());
        }
        consumeImageView.setImageResource (billInstance.getExpenditure ().getImageId ());
        consumeTypeSpinner.setText (billInstance.getExpenditure ().getName ());
        consumeTypeSpinner.setOnClickListener (v -> {
            MyDialog dialog = new MyDialog (root.getContext (),expenditures,position -> {
                Expenditure expenditure = expenditures.get (position);
                consumeImageView.setImageResource (expenditure.getImageId ());
                consumeTypeSpinner.setText (expenditure.getName ());
                billInstance.setExpenditureId (expenditure.getId ());
            });
            dialog.show ();
        });
        int index = 0;
        List<PayMethod> payMethods = LitePal.findAll (PayMethod.class);
        if(billInstance.getPayMethodId ()==null){
            billInstance.setPayMethodId (payMethods.get (0).getId ());
        }
        List<SpinnerData> data = new ArrayList<> ();
        for(int i=0;i<payMethods.size ();i++){
            PayMethod payMethod = payMethods.get (i);
            SpinnerData spinnerData = new SpinnerData ();
            spinnerData.setText (payMethod.getName ());
            spinnerData.setValue (payMethod.getId ().toString ());
            data.add (spinnerData);
            if(billInstance.getPayMethod ().getId ()==payMethod.getId ()){
                index = i;
            }
        }
        //支付方式下拉
        payMethodSpinner = root.findViewById (R.id.pay_method);
        ArrayAdapter<SpinnerData> payMethodAdapter = new ArrayAdapter<> (root.getContext (),android.R.layout.simple_spinner_item,data);
        //设置下拉列表的风格
        payMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payMethodSpinner.setAdapter (payMethodAdapter);
        payMethodSpinner.setSelection (index);
    }
}