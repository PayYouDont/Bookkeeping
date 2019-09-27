package com.example.bookkeeping.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookkeeping.MainActivity;
import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.util.HttpUtil;

import org.litepal.util.LogUtil;

/**
 * 自定义dialog
 */
public class VersionDialog extends Dialog implements View.OnClickListener {

    /**
     * 布局文件
     **/
    int layoutRes;

    /**
     * 上下文对象
     **/
    Context context;


    /**
     * 取消按钮
     **/
    private Button bt_cancal;

    /**
     * 更新按钮
     **/
    private Button bt_delect;
    private AppVersion appVersion;
    private UpdateListener updateListener;
    public interface UpdateListener{
        void onUpdate();
    }
    public VersionDialog(Context context) {
        super (context);
        this.context = context;
    }

    /**
     * 自定义布局的构造方法
     *
     * @param context
     * @param resLayout
     */
    public VersionDialog(Context context, int resLayout) {
        super (context);
        this.context = context;
        this.layoutRes = resLayout;
    }

    public VersionDialog(Context context, int theme, int resLayout, AppVersion appVersion) {
        super (context, theme);
        this.context = context;
        this.layoutRes = resLayout;
        this.appVersion = appVersion;
    }

    public VersionDialog(Context context, int theme, int resLayout, AppVersion appVersion, UpdateListener updateListener) {
        super (context, theme);
        this.context = context;
        this.layoutRes = resLayout;
        this.appVersion = appVersion;
        this.updateListener = updateListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        // 指定布局
        this.setContentView (layoutRes);
        TextView textView = findViewById (R.id.version_content);
        TextView titleView = findViewById (R.id.lay_view);
        textView.setText ("更新内容：\n\n" + appVersion.getUpdateLog ());
        titleView.setText ("发现新版本 " + appVersion.getVersionName ());
        // 根据id在布局中找到控件对象
        bt_cancal = findViewById (R.id.cancal);
        bt_delect = findViewById (R.id.update);
        // 为按钮绑定点击事件监听器
        bt_cancal.setOnClickListener (this);
        bt_delect.setOnClickListener (this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.update:
                if(updateListener!=null){
                    updateListener.onUpdate ();
                }
                this.dismiss ();
                break;

            // 取消按钮
            case R.id.cancal:
                this.dismiss ();

            default:
                break;
        }
    }
    /**
     * 操作  版本更新
     *
     */
    private void optUpdateApk( ) {
        try {
            //HttpUtil.downloadApk(appVersion);

        } catch (Exception e) {
            LogUtil.e ("VersionDialog",e);
        }
    }
}



