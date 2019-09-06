package com.example.bookkeeping.entity;

import android.widget.Spinner;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpinnerData {
    private String value = "";

    private String text = "";

    @Override
    public String toString() {
        return text;
    }
    /*spinnerID为R.id.xxx*/
    //取得value
    public static String getSpinnerSelVal(Spinner spinner){
        return ((SpinnerData)spinner.getSelectedItem()).getValue();
    }
    //取得text
    public static String getSpinnerSelName(Spinner spinner){
        return ((SpinnerData)spinner.getSelectedItem()).getText();
    }
}
