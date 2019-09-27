package com.example.bookkeeping.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MyProgressBar extends ProgressBar {
    private String text;
    private Paint mPaint;
    private Double expectedCount;
    private Double realCount;
    private TextView balanceText,balanceNumberText,spendText,spendNumberText;
    public MyProgressBar(Context context) {
        super (context);
        initText();
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }


    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw (canvas);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);
    }
    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);

    }
    //初始化，画笔
    private void initText(){
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.WHITE);
        this.mPaint.setTextSize (30);
    }
    private void setText(){
        setText(this.getProgress());
    }
    //设置文字内容
    private void setText(int progress){
        int i = (progress * 100)/this.getMax();
        this.text = i + "%";
        if(expectedCount!=null&&realCount!=null){
            DecimalFormat decimalFormat = new DecimalFormat ("#.00");
            if(i<100){//有剩余
                String balance = decimalFormat.format(expectedCount-realCount);
                balanceNumberText.setText (balance);
            }else{
                balanceNumberText.setText ("0");
            }
            spendNumberText.setText (decimalFormat.format (realCount));
        }
    }
}
