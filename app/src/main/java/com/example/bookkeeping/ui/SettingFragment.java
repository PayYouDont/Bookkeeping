package com.example.bookkeeping.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.bookkeeping.netty.NettyClient;
import com.example.bookkeeping.service.DownLoadDialogListener;
import com.example.bookkeeping.service.SyncTask;
import com.example.bookkeeping.service.VersionTask;
import com.example.bookkeeping.util.ReflectUtil;
import com.example.bookkeeping.util.StringUtil;
import com.example.bookkeeping.util.VersionUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.litepal.LitePal;
import org.litepal.util.LogUtil;

import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;
import lombok.Getter;

public class SettingFragment extends BaseFragment {
    private View root;
    private LinearLayout updateLayout,logLayout,descLayout,feedbackLayout,synchronizeLayout;
    private ImageView updateCircleImg;
    private TextView updateMsg;
    private FloatingActionButton scanBtn;
    private boolean hasNewVersion;
    private int REQUEST_CODE_SCAN = 111;
    private int port = 8080;
    private SyncTask syncTask;
    public final static int PROGRESS=1;
    public final static int START=0;
    public final static int FINISH=2;
    private DownLoadDialogListener downLoadDialogListener;
    @Getter
    private Handler handler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage (msg);
            switch (msg.what){
                case START:
                    downLoadDialogListener = new DownLoadDialogListener (getContext ());
                    break;
                case PROGRESS:
                    int progress = (int)msg.obj;
                    System.out.println ("进度："+progress);
                    //onProgress (progress);
                    //show ();
                    downLoadDialogListener.onProgress (progress);
                    break;
                case FINISH:
                    //onFinish ();
                    downLoadDialogListener.onProgress (100);
                    downLoadDialogListener.onFinish ();
                    Toast.makeText (getActivity (),"数据迁移完成!",Toast.LENGTH_SHORT).show ();
                    break;
                default:
                    break;
            }
        }
    };
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
        synchronizeLayout.setOnClickListener (v -> {
            //二维码窗口
            new QRDialog (getContext (),getUrl()).show ();
            //nettServerRun ();
            if(syncTask==null){
                InetSocketAddress address = new InetSocketAddress(getIpAddressString(), port);
                syncTask = new SyncTask (address);
                syncTask.start ();
            }
        });
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
                //new AlertDialog (getContext ()).show ();
                new SyncDataDialog (getContext (),() -> startMove(data)).show ();
            }
        }
    }
    private void startMove(Intent data){
        new Thread (()->{
            String content = data.getStringExtra(Constant.CODED_CONTENT);
            if(!StringUtil.isEmpty (content)&&content.indexOf (":")!=-1){
                String url = content.split (":")[0];
                InetSocketAddress address = new InetSocketAddress(url, port);
                NettyClient client = new NettyClient (address,handler);
                try {
                    client.start ();
                }catch (ConnectException ce){
                    Toast.makeText (getContext (),"两台手机需要在同一wifi下才能迁移数据！",Toast.LENGTH_SHORT).show ();
                }catch (Exception e){
                    LogUtil.e ("SettingFragment",e);
                }
            }else{
                Toast.makeText (getContext (),"扫描结果为:"+content,Toast.LENGTH_SHORT).show ();
            }
        }).start ();
    }
    private String getUrl(){
        String url = getIpAddressString ()+":"+port;
        return url;
    }
    private String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface.getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        if(syncTask!=null){
            syncTask.getNettyServer ().destroy ();
            syncTask.interrupt ();
        }
    }
}