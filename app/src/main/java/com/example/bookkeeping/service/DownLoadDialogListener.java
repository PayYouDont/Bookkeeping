package com.example.bookkeeping.service;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.bookkeeping.R;
import com.example.bookkeeping.ui.GADownloadingView;

import ezy.boost.update.OnDownloadListener;
import lombok.Getter;

public class DownLoadDialogListener extends Dialog implements OnDownloadListener {
    @Getter
    private GADownloadingView downloadingView;
    public DownLoadDialogListener(@NonNull Context context) {
        super (context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        View view = View.inflate (getContext (), R.layout.dialog_download,null);
        downloadingView = view.findViewById (R.id.ga_downloading);
        setContentView (view);
        downloadingView.performAnimation ();
    }

    @Override
    public void onStart() {}

    @Override
    public void onProgress(int progress) {
        if(!isShowing ()){
            show ();
        }
        if(downloadingView!=null){
            downloadingView.updateProgress (progress);
        }
    }

    @Override
    public void onFinish() {
        dismiss ();
    }
}
