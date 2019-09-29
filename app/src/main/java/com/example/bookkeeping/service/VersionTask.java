package com.example.bookkeeping.service;

import android.os.AsyncTask;

import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.util.HttpUtil;
import com.example.bookkeeping.util.ReflectUtil;

import org.litepal.util.LogUtil;

import java.io.IOException;

import lombok.Setter;

public class VersionTask extends AsyncTask<String,String,String> {
    @Setter
    private OnVersionListener onVersionListener;

    public VersionTask(OnVersionListener onVersionListener) {
        this.onVersionListener = onVersionListener;
    }

    @Override
    protected String doInBackground(String... params) {
        String checkVersionUrl = params[0];
        String versionJson = null;
        try {
            versionJson = HttpUtil.getVersion (checkVersionUrl);
        }catch (IOException e){
            LogUtil.e ("VersionTask",e);
        }
        return versionJson;
    }

    @Override
    protected void onPostExecute(String versionJson) {
        AppVersion appVersion = ReflectUtil.parseToAppversion (versionJson);
        if(onVersionListener!=null){
            onVersionListener.updateVersion (appVersion);
        }
    }
    public interface OnVersionListener{
        void updateVersion(AppVersion appVersion);
    }
}
