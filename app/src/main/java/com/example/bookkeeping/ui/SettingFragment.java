package com.example.bookkeeping.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookkeeping.MainActivity;
import com.example.bookkeeping.R;
import com.example.bookkeeping.adapter.BillAdapter;
import com.example.bookkeeping.entity.AppVersion;
import com.example.bookkeeping.entity.BaseFragment;
import com.example.bookkeeping.entity.Bill;
import com.example.bookkeeping.service.DownLoadService;
import com.example.bookkeeping.service.VersionTask;
import com.example.bookkeeping.util.HttpUtil;
import com.example.bookkeeping.util.VersionUtil;

import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.util.List;

public class SettingFragment extends BaseFragment {
    private View root;
    public String serverIP;
    private FragmentActivity activity;
    private DownLoadService.DownloadBinder downloadBinder;
    private LinearLayout updateLayout,logLayout,descLayout,feedbackLayout;
    private ImageView updateCircleImg;
    private TextView updateMsg;
    private ServiceConnection connection = new ServiceConnection () {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownLoadService.DownloadBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate (R.layout.fragment_setting, container, false);
        serverIP = getString (R.string.server_ip);
        activity = getActivity ();
        updateLayout = root.findViewById (R.id.setting_update_layout);
        logLayout = root.findViewById (R.id.setting_log_layout);
        descLayout = root.findViewById (R.id.setting_desc_layout);
        feedbackLayout = root.findViewById (R.id.setting_feedback_layout);
        updateCircleImg = root.findViewById (R.id.setting_update_circle_img);
        updateMsg = root.findViewById (R.id.setting_update_msg);
        setRootHeight (root);
        checkVersion ();
        return root;
    }
    @Override
    public void onDestroy() {
        super.onDestroy ();
        activity.unbindService (connection);
    }

    private void checkVersion() {
        Intent intent = new Intent (activity, DownLoadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.startForegroundService (intent);
        } else {
            activity.startService (intent);
        }
        activity.bindService (intent, connection, activity.BIND_AUTO_CREATE);
        if (ContextCompat.checkSelfPermission (activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions (activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        new VersionTask (appVersion -> updateView (appVersion)).execute (serverIP + "/getVersion");
    }
    private void updateView(AppVersion appVersion){
        int currentVersionCode = VersionUtil.getVersion (activity);
        if (appVersion.getVersionCode () > currentVersionCode) {
            updateCircleImg.setVisibility (View.VISIBLE);
            updateMsg.setText (getString (R.string.new_version_label));
            updateLayout.setOnClickListener (v -> {
                VersionUtil.updateApk (appVersion, activity, () -> {
                    String apkUrl = serverIP + "/downloadApk?apkUrl=" + appVersion.getApkUrl ();
                    if(downloadBinder!=null){
                        downloadBinder.setAppVersion (appVersion);
                        downloadBinder.setContext (activity);
                        downloadBinder.startDownload (apkUrl);
                    }
                });
            });
        }else{
            updateCircleImg.setVisibility (View.GONE);
            updateMsg.setText (getString (R.string.version_label));
            appVersion.saveOrUpdate ("id=1");
        }
        logLayout.setOnClickListener (v -> {
            VersionDialog versionDialog = new VersionDialog (activity,R.layout.dialog_update,LitePal.find (AppVersion.class,1));
            versionDialog.show ();
        });
        descLayout.setOnClickListener (v -> Toast.makeText (getContext (),"待开发",Toast.LENGTH_SHORT).show ());
        feedbackLayout.setOnClickListener (v -> Toast.makeText (getContext (),"等待开发",Toast.LENGTH_SHORT).show ());
    }
}