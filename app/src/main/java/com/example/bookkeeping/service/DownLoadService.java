/*
package com.example.bookkeeping.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.DownloadListener;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public class DownLoadService extends Service {
    private DownloadTask downloadTask;
    private String downloadUrl;
    private DownloadListener downloadListener = new DownloadListener () {
        @Override
        public void onProgress(int progress) {
            getNotificationManager ().notify (1,getNotification ("下载中...",progress));
        }
        @Override
        public void onSuccess() {
            downloadTask = null;
            //下载成功关闭前台服务通知关闭，并创建一个下载成功的通知
            stopForeground (true);
            getNotificationManager ().notify (1,getNotification ("点击安装",-1));
            Toast.makeText (DownLoadService.this,"下载成功！",Toast.LENGTH_SHORT).show ();
            String fileName = "Bookkeeping_V"+mBinder.appVersion.getVersionName ()+".apk";
            String directory = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).getPath ()+File.separator;
            String apkPath = directory + fileName;
            installApk (mBinder.context,apkPath);
            mBinder.appVersion.saveOrUpdate ("id=1");
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground (true);
            getNotificationManager ().notify (1,getNotification ("下载失败！",-1));
            Toast.makeText (DownLoadService.this,"下载失败！",Toast.LENGTH_SHORT).show ();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            stopForeground (true);
            Toast.makeText (DownLoadService.this,"暂停下载",Toast.LENGTH_SHORT).show ();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground (true);
            Toast.makeText (DownLoadService.this,"取消下载",Toast.LENGTH_SHORT).show ();
        }

    };
    public DownLoadService() {}
    private NotificationManager getNotificationManager() {
        return (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getNotification(String title,int progress) {
        Intent intent = new Intent (this, DownLoadService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity (this,0,intent,0);
        Notification.Builder builder = new Notification.Builder (this.getApplicationContext())
                .setContentIntent (pendingIntent)
                .setContentTitle (title)
                .setWhen (System.currentTimeMillis ())
                .setShowWhen (true)
                .setVibrate(new long[]{0})
                .setSmallIcon (R.mipmap.ic_launcher)
                .setLargeIcon (BitmapFactory.decodeResource (getResources (),R.mipmap.ic_launcher));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel ("Bookkeeping","账单", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否在久按桌面图标时显示此渠道的通知
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notificationChannel.enableVibration (false);
            notificationChannel.setVibrationPattern (new long[]{0});
            NotificationManager manager = getNotificationManager ();
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId (notificationChannel.getId ());
        }
        if(progress>=0){
            builder.setContentText (progress+"%");
            builder.setProgress (100,progress,false);
        }
        return builder.build(); // 获取构建好的Notification
    }
    // 安装Apk
    public static void installApk(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, "com.example.bookkeeping.fileprovider", file);
            //Granting Temporary Permissions to a URI
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
    private DownloadBinder mBinder = new DownloadBinder ();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Getter
    @Setter
    public class DownloadBinder extends Binder{
        private AppVersion appVersion;
        private Context context;
        public void startDownload(String url){
            if(downloadTask==null){
                downloadUrl = url;
                downloadTask = new DownloadTask (downloadListener,appVersion);
                downloadTask.execute (downloadUrl);
                startForeground (1,getNotification ("下载中...",0));
                Toast.makeText (DownLoadService.this,"下载中...",Toast.LENGTH_SHORT).show ();
            }
        }
        public void pauseDownload(){
            if(downloadTask!=null){
                downloadTask.pausedDownload ();
            }
        }
        public void cancelDownload(){
            //取消下载时需要删除文件并关闭通知
            if(downloadTask!=null){
                downloadTask.cancelDownload ();
            }
            if(downloadUrl!=null){
                String fileName = downloadUrl.substring (downloadUrl.lastIndexOf ("/"));
                String directory = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).getPath ();
                File file = new File (directory+fileName);
                if(file.exists ()){
                    file.delete ();
                }
            }
            getNotificationManager ().cancel (1);
            stopForeground (true);
            Toast.makeText (DownLoadService.this,"取消下载",Toast.LENGTH_SHORT).show ();
        }
    }
}
*/
