package com.example.bookkeeping.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.bookkeeping.R;

import ezy.boost.update.OnDownloadListener;
import lombok.Getter;

public class DownLoadDialog extends Dialog implements OnDownloadListener {
    @Getter
    private GADownloadingView downloadingView;
    public DownLoadDialog(@NonNull Context context) {
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
    public void onStart() {
        downloadingView.updateProgress (0);
    }

    @Override
    public void onProgress(int progress) {
        show ();
        if(downloadingView!=null){
            downloadingView.updateProgress (progress);
        }
    }

    @Override
    public void onFinish() {
        dismiss ();
    }
}
