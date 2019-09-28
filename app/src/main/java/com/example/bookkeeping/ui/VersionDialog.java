package com.example.bookkeeping.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.AppVersion;

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

    public VersionDialog(Context context, int resLayout, AppVersion appVersion) {
        super (context);
        this.context = context;
        this.layoutRes = resLayout;
        this.appVersion = appVersion;
    }

    public VersionDialog(Context context, int resLayout, AppVersion appVersion, UpdateListener updateListener) {
        super (context);
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
        bt_cancal = findViewById (R.id.cancal);
        bt_delect = findViewById (R.id.update);
        if(updateListener!=null){
            textView.setText ("更新内容：\n\n" + appVersion.getUpdateLog ());
            titleView.setText ("发现新版本 V" + appVersion.getVersionName ());
            // 为按钮绑定点击事件监听器
            bt_delect.setVisibility (View.VISIBLE);
            bt_cancal.setText ("稍后");
        }else {
            textView.setText ("当前版本更新日志：\n\n" + appVersion.getUpdateLog ());
            titleView.setText ("当前版本 V" + appVersion.getVersionName ());
            bt_delect.setVisibility (View.GONE);
            bt_cancal.setText ("确定");
        }
        bt_cancal.setOnClickListener (this);
        bt_delect.setOnClickListener (this);
        Window window = getWindow ();
        WindowManager.LayoutParams p = window.getAttributes ();
        WindowManager manager = getWindow ().getWindowManager ();
        Point size = new Point ();
        manager.getDefaultDisplay ().getSize (size);
        p.width = size.x - 100;
        window.setAttributes (p);
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



