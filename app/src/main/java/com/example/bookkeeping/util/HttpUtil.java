package com.example.bookkeeping.util;

import com.example.bookkeeping.MainActivity;
import com.example.bookkeeping.entity.AppVersion;

import org.litepal.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    public static String readParse(String urlPath) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        return new String(outStream.toByteArray());
    }
    public static void downloadApk(AppVersion appVersion) throws Exception{
        String apkUrl = MainActivity.serverIP + "/downloadApk?apkUrl=" + appVersion.getApkUrl ();
        new Thread(() -> {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();//获取输入流
                    FileOutputStream fileOutputStream = null;//文件输出流
                    if (is != null) {
                        String apkName = "Bookkeeping_V"+appVersion.getVersionName ()+".apk";
                        fileOutputStream = new FileOutputStream(FileUtil.createFile(apkName));
                        byte[] buf = new byte[1024];
                        int ch;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                        }
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }
            } catch (Exception e) {
                LogUtil.e ("HttpUtil",e);
            }
        }).start();
    }
    public static String getVersion(String url) throws IOException {
        OkHttpClient client = new OkHttpClient ();
        Request request = new Request.Builder ().url (url).build ();
        Response response = client.newCall (request).execute ();
        if(response.isSuccessful ()){
            String verson = response.body ().string ();
            response.close ();
            return verson;
        }
        return null;
    }
}
