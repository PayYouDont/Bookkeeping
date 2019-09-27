package com.example.bookkeeping;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.Expenditure;
import com.example.bookkeeping.entity.PayMethod;
import com.example.bookkeeping.service.DownLoadService;
import com.example.bookkeeping.util.HttpUtil;
import com.example.bookkeeping.util.VersionUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    public static Integer navBarHeight;
    public static String serverIP;
    private DownLoadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection () {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownLoadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        BottomNavigationView navView = findViewById (R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder (R.id.navigation_home, R.id.navigation_edit, R.id.navigation_history).build ();
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController (this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController (navView, navController);
        //初始化消费类型表
        initExpenditureTable ();
        //初始化支付方式表
        initPayMothedTable ();
        navBarHeight = getNavigationBarHeight (this);
        serverIP = getString (R.string.server_ip);
        checkVersion ();
        Intent intent = new Intent (this, DownLoadService.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService (intent);
        }else {
            startService(intent);
        }
        bindService (intent,connection,BIND_AUTO_CREATE);
        if(ContextCompat.checkSelfPermission (MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions (MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText (this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show ();
                    finish ();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        unbindService (connection);
    }

    public static int getNavigationBarHeight(Activity mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
    //初始化Expenditure表
    public void initExpenditureTable(){
        List<Expenditure> expenditures = LitePal.findAll (Expenditure.class);
        Map<String,Integer> expenditureData = getExpenditureData();
        if(expenditures==null||expenditures.size ()==0||expenditures.get (0).getImageId ()!=expenditureData.get ("消费")){
            expenditureData.forEach ((name,imageId) -> {
                Expenditure expenditure = new Expenditure ();
                expenditure.setName (name);
                expenditure.setImageId (imageId);
                expenditure.saveOrUpdate ("name='"+name+"'");
            });
        }
    }
    //初始化PayMothed
    public void initPayMothedTable(){
        List<PayMethod> payMethods = LitePal.findAll (PayMethod.class);
        if(payMethods==null||payMethods.size ()==0){
            String[] names = {"现金","支付宝","微信","银行卡","信用卡","其他"};
            for(int i=0;i<names.length;i++){
                PayMethod payMethod = new PayMethod ();
                payMethod.setName (names[i]);
                payMethod.save ();
            }
        }
    }
    private Map<String,Integer> getExpenditureData(){
        Map<String,Integer> map = new LinkedHashMap<> ();
        map.put ("消费",R.drawable.ic_edit_consume);
        map.put ("转账",R.drawable.ic_edit_transfer);
        map.put ("餐饮",R.drawable.ic_edit_food);
        map.put ("交通",R.drawable.ic_edit_traffic);
        map.put ("娱乐",R.drawable.ic_edit_recreation);
        map.put ("购物",R.drawable.ic_edit_shopping);
        map.put ("通讯",R.drawable.ic_edit_correspondence);
        map.put ("AA",R.drawable.ic_edit_aa);
        map.put ("红包",R.drawable.ic_edit_redenvelope);
        map.put ("取款",R.drawable.ic_edit_withdrawal);
        map.put ("生活",R.drawable.ic_edit_life);
        map.put ("租房",R.drawable.ic_edit_renting);
        map.put ("医疗",R.drawable.ic_edit_life);
        map.put ("教育",R.drawable.ic_edit_education);
        map.put ("其他",R.drawable.ic_edit_other);
        return map;
    }
    private void checkVersion(){
        AsyncTask asyncTask = new AsyncTask () {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    return HttpUtil.getVersion (serverIP+"/getVersion");
                }catch (Exception e){
                    LogUtil.e ("MainActivity.AsyncTask",e);
                }
                return null;
            }
        };
        asyncTask.execute ();
        try {
            String data = asyncTask.get ().toString ();
            JSONObject object = new JSONObject (data);
            Integer versionCode = object.getInt ("versionCode");
            int currentVersionCode = VersionUtil.getVersion (MainActivity.this);
            if(versionCode>currentVersionCode){
                AppVersion appVersion = new AppVersion ();
                appVersion.setVersionCode (versionCode);
                appVersion.setVersionName (object.getString ("versionName"));
                appVersion.setUpdateLog (object.getString ("updateLog"));
                appVersion.setForcedUpdate (object.getBoolean ("forcedUpdate"));
                appVersion.setApkUrl (object.getString ("apkUrl"));
                appVersion.setApkSize (object.getDouble ("apkSize"));
                appVersion.setMd5 (object.getString ("md5"));
                VersionUtil.updateApk (appVersion,this,() -> {
                    String apkUrl = MainActivity.serverIP + "/downloadApk?apkUrl=" + appVersion.getApkUrl ();
                    downloadBinder.setAppVersion (appVersion);
                    downloadBinder.setContext (MainActivity.this);
                    downloadBinder.startDownload (apkUrl);
                });
            }
        }catch (Exception e){
            LogUtil.e ("MainActivity",e);
        }

    }
}
