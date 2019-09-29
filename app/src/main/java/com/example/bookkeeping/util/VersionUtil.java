package com.example.bookkeeping.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import org.litepal.util.LogUtil;

public class VersionUtil {
    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 2 * 获取版本
     */
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),0);
            int versioncode = info.versionCode;
            return versioncode;
        } catch (Exception e) {
            LogUtil.e ("VersionUtils",e);
        }
        return 0;
    }

    /**
     * 2 * 获取版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            LogUtil.e ("VersionUtils",e);
        }
        return "";
    }
    /*public static void updateApk(AppVersion appVersion, FragmentActivity activity, VersionDialog.UpdateListener listener){
        try {
            VersionDialog versionDialog = new VersionDialog (activity,R.layout.dialog_update,appVersion,listener);
            versionDialog.show ();
        }catch (Exception e){
            LogUtil.e ("VersionUtils",e);
        }
    }*/
}


