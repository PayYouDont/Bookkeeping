package com.example.bookkeeping;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bookkeeping.entity.Expenditure;
import com.example.bookkeeping.entity.PayMethod;
import com.example.bookkeeping.ui.edit.EditFragment;
import com.example.bookkeeping.util.ReflectUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.litepal.LitePal;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    public static Integer navBarHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        BottomNavigationView navView = findViewById (R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder (
                R.id.navigation_home, R.id.navigation_edit, R.id.navigation_history)
                .build ();
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController (this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController (navView, navController);
        navController.addOnDestinationChangedListener ((controller, destination, arguments) -> {

        });
       /* View view = findViewById (R.id.navigation_edit);
        View.OnClickListener onClickListener = view*/
        //初始化消费类型表
        initExpenditureTable ();
        //初始化支付方式表
        initPayMothedTable ();
        navBarHeight = getNavigationBarHeight (this);
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
}
