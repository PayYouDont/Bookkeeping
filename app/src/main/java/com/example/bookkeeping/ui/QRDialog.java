package com.example.bookkeeping.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.util.NetWorkUtil;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class QRDialog extends AlertDialog {
    protected QRDialog(Context context,String url) {
        super (context);
        Bitmap bitmap = CodeCreator.createQRCode(url, 150, 150, null);
        LayoutInflater inflater = LayoutInflater.from (context);
        View view = inflater.inflate (R.layout.qr_dialog,null);
        ImageView imageView = view.findViewById (R.id.qr_img);
        imageView.setImageBitmap (bitmap);
        TextView textView = view.findViewById (R.id.qr_wifi_name);
        textView.setText ("当前wifi："+ NetWorkUtil.getWIFISSID (context));
        setView(view);
        setButton(DialogInterface.BUTTON_POSITIVE, "确定",(dialog, which) -> dismiss ());
    }
}
