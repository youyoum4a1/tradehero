package com.tradehero.th.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class APKDownloadNInstaller extends BroadcastReceiver {
    private static final String APK_FOLDER = "apks";
    private static final String APK_LOCAL_NAME = "update.apk";

    private String save_path;
    private Activity activity;

    public void downloadApk(Activity activity, String apkUrl){
        this.activity = activity;
        DownloadManager downloadManager  = (DownloadManager)(activity.getSystemService(activity.DOWNLOAD_SERVICE));

        String dir = createFolder(APK_FOLDER);
        save_path = dir +"/" + APK_LOCAL_NAME;

        File f = new File(dir + "/" + APK_LOCAL_NAME);
        if(f.exists()) f.delete();


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));

        request.setDestinationInExternalPublicDir(APK_FOLDER, APK_LOCAL_NAME);
        request.allowScanningByMediaScanner();//表示允许MediaScanner扫描到这个文件，默认不允许。
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        activity.registerReceiver(this, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Toast.makeText(activity, "正在为您下载在线交易安装包...", Toast.LENGTH_LONG).show();
        downloadManager.enqueue(request);
    }

    private String createFolder(String dir) {
        File folder = Environment.getExternalStoragePublicDirectory(dir);
        if (folder.exists()) {
            if (!folder.isDirectory()) {
                folder.delete();
                folder.mkdirs();
            }
        }
        else {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(activity, "下载成功！", Toast.LENGTH_LONG).show();
        downComplete(save_path);
    }

    private void downComplete(String filePath){
        File file =  new File(filePath);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");//向用户显示数据
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//以新压入栈
        intent.addCategory("android.intent.category.DEFAULT");

        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        activity.startActivity(intent);
    }
}
