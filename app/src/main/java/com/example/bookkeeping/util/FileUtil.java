package com.example.bookkeeping.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {
    /**
     * 创建一个文件
     * @param FileName 文件名
     * @return
     */
    public static File createFile(String FileName) {
        String path;
        if(VersionUtil.hasSdcard ()){
            path = Environment.getExternalStorageDirectory().toString() +File.separator+"bookkepping";
        }else{
            path = "";
        }
        File file = new File(path);
        /**
         *如果文件夹不存在就创建
         */
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(path, FileName);
    }
}
