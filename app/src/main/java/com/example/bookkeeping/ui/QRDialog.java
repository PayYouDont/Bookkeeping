package com.example.bookkeeping.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.bookkeeping.R;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class QRDialog extends AlertDialog {
    protected QRDialog(Context context,String url) {
        super (context);
        Bitmap bitmap = CodeCreator.createQRCode(url, 150, 150, null);
        LayoutInflater inflater = LayoutInflater.from (context);
        View view = inflater.inflate (R.layout.qr_dialog,null);
        ImageView imageView = view.findViewById (R.id.qr_img);
        imageView.setImageBitmap (bitmap);
        setView(view);
        setButton(DialogInterface.BUTTON_POSITIVE, "确定",(dialog, which) -> dismiss ());
    }
}
