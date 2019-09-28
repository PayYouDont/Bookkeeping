package com.example.bookkeeping.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

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
