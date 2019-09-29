package com.example.bookkeeping.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.bookkeeping.entity.AppVersion;

public class VersionLogDialog extends AlertDialog{

    public VersionLogDialog(Context context, AppVersion appVersion) {
        super (context);
        setTitle("当前版本更新日志：");
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        float density = context.getResources().getDisplayMetrics().density;
        TextView tv = new TextView(context);
        tv.setMovementMethod(new ScrollingMovementMethod ());
        tv.setVerticalScrollBarEnabled(true);
        tv.setTextSize(14);
        tv.setMaxHeight((int) (250 * density));
        setView(tv, (int) (25 * density), (int) (15 * density), (int) (25 * density), 0);
        String content = String.format("当前版本：%1$s\n版本大小：%2$s\n\n更新内容\n%3$s", appVersion.getVersionName (), appVersion.getApkSize (), appVersion.getUpdateLog ());
        tv.setText(content);
        setButton(DialogInterface.BUTTON_POSITIVE, "确定",(dialog, which) -> dismiss ());
    }
}
