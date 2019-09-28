package com.example.bookkeeping.service;

import android.os.AsyncTask;

import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.util.HttpUtil;

import org.json.JSONObject;
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
        AppVersion appVersion = null;
        if(versionJson!=null){
            try {
                JSONObject object = new JSONObject (versionJson);
                appVersion = new AppVersion ();
                appVersion.setVersionCode (object.getInt ("versionCode"));
                appVersion.setVersionName (object.getString ("versionName"));
                appVersion.setUpdateLog (object.getString ("updateLog"));
                appVersion.setForcedUpdate (object.getBoolean ("forcedUpdate"));
                appVersion.setApkUrl (object.getString ("apkUrl"));
                appVersion.setApkSize (object.getDouble ("apkSize"));
                appVersion.setMd5 (object.getString ("md5"));
            }catch (Exception e){
                LogUtil.e ("VersionTask",e);
            }
        }
        if(onVersionListener!=null){
            onVersionListener.updateVersion (appVersion);
        }
    }
    public interface OnVersionListener{
        void updateVersion(AppVersion appVersion);
    }
}
