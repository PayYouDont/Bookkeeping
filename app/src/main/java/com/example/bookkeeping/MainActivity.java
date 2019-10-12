package com.example.bookkeeping;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.entity.Expenditure;
import com.example.bookkeeping.entity.PayMethod;
import com.example.bookkeeping.service.DownLoadDialogListener;
import com.example.bookkeeping.service.VersionTask;
import com.example.bookkeeping.ui.EditFragment;
import com.example.bookkeeping.ui.HistoryFragment;
import com.example.bookkeeping.ui.HomeFragment;
import com.example.bookkeeping.ui.SettingFragment;
import com.example.bookkeeping.util.PermissionUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.litepal.LitePal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.alexbykov.nopermission.PermissionHelper;

public class MainActivity extends AppCompatActivity{
    public static Integer navBarHeight;
    BottomNavigationView navView;
    public static String serverIP;
    private PermissionHelper permissionHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        navView = findViewById (R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder (R.id.navigation_home, R.id.navigation_history,R.id.navigation_setting).build ();
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController (this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController (navView, navController);
        serverIP = getString (R.string.server_ip);
        //初始化消费类型表
        initExpenditureTable ();
        //初始化支付方式表
        initPayMothedTable ();
        initVersionInfo();
        navBarHeight = getNavigationBarHeight (this);
        SettingFragment.checkVersion (new DownLoadDialogListener (this));
        Request();
        permissionHelper = new PermissionHelper(this);
        getWifiSSid();
    }
    private void getWifiSSid() {
        permissionHelper.check(Manifest.permission.ACCESS_FINE_LOCATION).onSuccess(() -> {

        }).onDenied(() -> {
            Toast.makeText (this,"权限被拒绝！将无法获取到WiFi信息!",Toast.LENGTH_SHORT).show ();
        }).onNeverAskAgain(() -> {{
            Toast.makeText (this,"权限被拒绝！将无法获取到WiFi信息,下次不会再询问了！",Toast.LENGTH_SHORT).show ();
        }}).run();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    void Request() {
        //获取相机拍摄读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //版本判断
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(EditFragment.isCreated){
                Fragment fragment = null;
                FragmentTransaction transaction = getSupportFragmentManager ().beginTransaction ();
                if(navView.getSelectedItemId ()==R.id.navigation_home){
                    fragment = new HomeFragment ();
                }else if(navView.getSelectedItemId ()==R.id.navigation_history){
                    fragment = new HistoryFragment ();
                }
                transaction.replace (R.id.nav_host_fragment,fragment).commit ();
                EditFragment.isCreated = false;
                return true;
            }else if(navView.getSelectedItemId ()==R.id.navigation_history){
                View view = findViewById (R.id.navigation_home);
                view.performClick();
                return true;
            }
        }
        return super.onKeyDown (keyCode, event);
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
    public void initVersionInfo(){
        AppVersion appVersion = LitePal.find (AppVersion.class,1);
        if(appVersion==null){
            new VersionTask (version -> {
                version.setId (1);
                version.save();
            }).execute (serverIP + "/getVersion");
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
}
