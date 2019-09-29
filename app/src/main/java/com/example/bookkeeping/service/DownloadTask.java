/*
package com.example.bookkeeping.service;

import android.os.AsyncTask;
import android.os.Environment;

import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.DownloadListener;

import org.litepal.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<String,Integer,Integer> {
    public static final int TYPE_SUCCESS=0;
    public static final int TYPE_FAILED=1;
    public static final int TYPE_PAUSED=2;
    public static final int TYPE_CANCELED=3;
    private DownloadListener downloadListener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress;
    private AppVersion appVersion;
    public DownloadTask(DownloadListener downloadListener,AppVersion appVersion) {
        this.downloadListener = downloadListener;
        this.appVersion = appVersion;
    }

    @Override
    protected Integer doInBackground(String... params) {
        return downloadApk2 (params);
    }
    private int downloadApk2(String... params){
        InputStream in = null;
        OutputStream out = null;
        File file = null;
        try {
            String downloadUrl = params[0];
            String fileName = "Bookkeeping_V"+appVersion.getVersionName ()+".apk";
            String directory = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).getPath ()+File.separator;
            file = new File (directory+fileName);
            long downloadedLength = 0;//已下载文件长度
            if(file.exists ()){
                downloadedLength = file.length ();
            }
            long contentLentgh = getContentLength (downloadUrl);
            if(contentLentgh==0) {
                return TYPE_FAILED;
            }
            if(downloadedLength==contentLentgh){
                return TYPE_SUCCESS;
            }else {
                file.delete ();
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder ().url (downloadUrl).build ();
            Response response = client.newCall (request).execute ();
            if(response != null){
                in = response.body ().byteStream ();
                out = new FileOutputStream (file);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = in.read (b))!=-1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total += len;
                        out.write (b,0,len);
                        int progress = (int)(total*100/contentLentgh);
                        publishProgress (progress);
                    }
                }
                return TYPE_SUCCESS;
            }
        }catch (Exception e){
            LogUtil.e ("DownloadTask",e);
        }finally {
            try {
                if(in!=null){
                    in.close ();
                }
                if(out!=null){
                    out.close ();
                }
                if(isCanceled&&file!=null){
                    file.delete ();
                }
            }catch (Exception e){
                LogUtil.e ("DownloadTask",e);
            }
        }
        return TYPE_FAILED;
    }
    private int downloadApk(String... params){
        InputStream in = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0;//已下载文件长度
            String downloadUrl = params[0];
            //String fileName = downloadUrl.substring (downloadUrl.lastIndexOf ("/"));
            String fileName = "Bookkeeping_V"+appVersion.getVersionName ()+".apk";
            String directory = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).getPath ()+File.separator;
            file = new File (directory+fileName);
            if(file.exists ()){
                downloadedLength = file.length ();
            }
            long contentLentgh = getContentLength (downloadUrl);
            if(contentLentgh==0){
                return TYPE_FAILED;
            }else if(contentLentgh == downloadedLength){//下载已经完成
                return TYPE_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder ()
                    //.header("RANGE", "bytes=" + downloadedLength + "-")//断点下载，指定从哪个字节开始下载
                    .url (downloadUrl).build ();
            Response response = client.newCall (request).execute ();
            if(response != null){
                in = response.body ().byteStream ();
                savedFile = new RandomAccessFile (file,"rw");
                savedFile.seek (downloadedLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = in.read (b))!=-1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }else if(isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total += len;
                        savedFile.write (b,0,len);
                        int progress = (int)((total + downloadedLength)*100/contentLentgh);
                        publishProgress (progress);
                    }
                }
                return TYPE_SUCCESS;
            }
        }catch (Exception e){
            LogUtil.e ("DownloadTask",e);
        }finally {
            try {
                if(in!=null){
                    in.close ();
                }
                if(savedFile!=null){
                    savedFile.close ();
                }
                if(isCanceled&&file!=null){
                    file.delete ();
                }
            }catch (Exception e){
                LogUtil.e ("DownloadTask",e);
            }
        }
        return TYPE_FAILED;
    }
    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                downloadListener.onSuccess ();
                break;
            case TYPE_FAILED:
                downloadListener.onFailed ();
                break;
            case TYPE_PAUSED:
                downloadListener.onPaused ();
                break;
            case TYPE_CANCELED:
                downloadListener.onCanceled ();
                break;
            default:
                break;
        }
    }

    public void pausedDownload(){
        isPaused = true;
    }
    public void cancelDownload(){
        isCanceled = true;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if(progress>lastProgress){
            downloadListener.onProgress (progress);
            lastProgress = progress;
        }
    }

    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient ();
        Request request = new Request.Builder().addHeader ("Accept-Encoding", "identity").url (downloadUrl).build ();
        Response response = client.newCall (request).execute ();
        if(response !=null && response.isSuccessful ()){
            long contentLength = response.body ().contentLength ();
            response.body ().close ();
            return contentLength;
        }
        return 0;
    }
}
*/
