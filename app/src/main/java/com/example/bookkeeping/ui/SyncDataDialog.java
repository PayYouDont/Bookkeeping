package com.example.bookkeeping.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.bookkeeping.entity.AppVersion;

import lombok.Setter;

public class SyncDataDialog{
    private OnSyncListener onSyncListener;
    private AlertDialog alertDialog;
    public SyncDataDialog(Context context,OnSyncListener onSyncListener) {
        float density = context.getResources().getDisplayMetrics().density;
        TextView tv = new TextView(context);
        tv.setMovementMethod(new ScrollingMovementMethod ());
        tv.setVerticalScrollBarEnabled(true);
        tv.setTextSize(14);
        tv.setMaxHeight((int) (250 * density));
        String content = String.format("是否现在开始同步？");
        tv.setText(content);
        AlertDialog.Builder builder = new AlertDialog.Builder (context);
        builder.setPositiveButton("确认",(dialogInterface, i) ->{
            alertDialog.dismiss ();
            onSyncListener.startSync ();
        });
        builder.setNegativeButton ("取消",(dialogInterface, i) -> alertDialog.dismiss ());
        alertDialog = builder.create ();
        alertDialog.setView (tv, (int) (25 * density), (int) (15 * density), (int) (25 * density), 0);
    }
    public interface OnSyncListener{
        void startSync();
    }
    public void show(){
        alertDialog.show ();
    }
}
