package com.example.bookkeeping.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookkeeping.MainActivity;
import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.BaseFragment;
import com.example.bookkeeping.service.DownLoadDialogListener;
import com.example.bookkeeping.service.VersionTask;
import com.example.bookkeeping.util.ReflectUtil;
import com.example.bookkeeping.util.VersionUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;

public class SettingFragment extends BaseFragment {
    private View root;
    private LinearLayout updateLayout,logLayout,descLayout,feedbackLayout,synchronizeLayout;
    private ImageView updateCircleImg;
    private TextView updateMsg;
    private FloatingActionButton scanBtn;
    private boolean hasNewVersion;
    private int REQUEST_CODE_SCAN = 111;
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.fragment_setting, container, false);
        updateLayout = root.findViewById (R.id.setting_update_layout);
        updateLayout.setOnClickListener (v -> {
            if (hasNewVersion) {
                checkVersion(new DownLoadDialogListener (getContext ()));
            }
        });
        logLayout = root.findViewById (R.id.setting_log_layout);
        descLayout = root.findViewById (R.id.setting_desc_layout);
        feedbackLayout = root.findViewById (R.id.setting_feedback_layout);
        synchronizeLayout = root.findViewById (R.id.setting_synchronize_layout);
        scanBtn = root.findViewById (R.id.setting_scan_btn);
        updateCircleImg = root.findViewById (R.id.setting_update_circle_img);
        updateMsg = root.findViewById (R.id.setting_update_msg);
        setRootHeight (root);
        new VersionTask (appVersion -> updateView (appVersion)).execute (MainActivity.serverIP + "/getVersion");
        return root;
    }
    public static void checkVersion(DownLoadDialogListener downLoadDialogListener) {
        String mCheckUrl = MainActivity.serverIP + "/getVersion";
        UpdateManager.create (downLoadDialogListener.getContext ()).setUrl (mCheckUrl).setParser (source -> {
            UpdateInfo info = ReflectUtil.parseToUpdateInfo (source);
            String apkUrl = MainActivity.serverIP + "/downloadApk?apkUrl=" + info.url;
            info.url = apkUrl;
            info.hasUpdate = info.versionCode>VersionUtil.getVersion (downLoadDialogListener.getContext ());
            return info;
        }).setOnDownloadListener (downLoadDialogListener).check ();
    }
    private void updateView(AppVersion appVersion){
        hasNewVersion = appVersion.getVersionCode ()>VersionUtil.getVersion (getContext ());
        if(hasNewVersion){
            updateCircleImg.setVisibility (View.VISIBLE);
            updateMsg.setText (getString (R.string.new_version_label));
            appVersion.saveOrUpdate ("id=1");
        }else {
            updateCircleImg.setVisibility (View.GONE);
            updateMsg.setText (getString (R.string.version_label));
        }
        logLayout.setOnClickListener (v -> new VersionLogDialog (getContext (),LitePal.find (AppVersion.class,1)).show ());
        synchronizeLayout.setOnClickListener (v -> new QRDialog (getContext (),getUrl()).show ());
        descLayout.setOnClickListener (v -> Toast.makeText (getContext (),"待开发",Toast.LENGTH_SHORT).show ());
        feedbackLayout.setOnClickListener (v -> Toast.makeText (getContext (),"等待开发",Toast.LENGTH_SHORT).show ());
        scanBtn.setOnClickListener (v -> {
            //调用相机扫描
            Intent intent = new Intent(getContext (), CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN);
        });
    }
    //接收扫描结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Toast.makeText (getContext (),"扫描结果为:"+content,Toast.LENGTH_SHORT).show ();
            }
        }
    }

    private String getUrl(){
        String url = getIpAddressString ();
        return url;
    }
    private String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LogUtil.e ("SettingFragment",e);
        }
        return "0.0.0.0";
    }
}