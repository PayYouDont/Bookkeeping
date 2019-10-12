package com.example.bookkeeping.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.bookkeeping.entity.AppVersion;

public class SyncDataDialog extends AlertDialog{
    private OnSyncListener onSyncListener;
    public SyncDataDialog(Context context,OnSyncListener onSyncListener) {
        super (context);
        this.onSyncListener = onSyncListener;
        setTitle("是否同步：");
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        float density = context.getResources().getDisplayMetrics().density;
        TextView tv = new TextView(context);
        tv.setMovementMethod(new ScrollingMovementMethod ());
        tv.setVerticalScrollBarEnabled(true);
        tv.setTextSize(14);
        tv.setMaxHeight((int) (250 * density));
        setView(tv, (int) (25 * density), (int) (15 * density), (int) (25 * density), 0);
        String content = String.format("是否现在开始同步？");
        tv.setText(content);
        setButton(DialogInterface.BUTTON_POSITIVE, "确定",(dialog, which) -> {
            dismiss ();
            onSyncListener.startSync ();
        });
    }
    public interface OnSyncListener{
        void startSync();
    }
}
