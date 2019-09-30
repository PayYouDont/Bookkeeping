package com.example.bookkeeping.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bookkeeping.MainActivity;
import com.example.bookkeeping.R;
import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.BaseFragment;
import com.example.bookkeeping.service.DownLoadDialogListener;
import com.example.bookkeeping.service.VersionTask;
import com.example.bookkeeping.util.ReflectUtil;
import com.example.bookkeeping.util.VersionUtil;

import org.litepal.LitePal;

import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;

public class SettingFragment extends BaseFragment {
    private View root;
    private LinearLayout updateLayout,logLayout,descLayout,feedbackLayout;
    private ImageView updateCircleImg;
    private TextView updateMsg;
    private boolean hasNewVersion;
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
        descLayout.setOnClickListener (v -> Toast.makeText (getContext (),"待开发",Toast.LENGTH_SHORT).show ());
        feedbackLayout.setOnClickListener (v -> Toast.makeText (getContext (),"等待开发",Toast.LENGTH_SHORT).show ());
    }
}