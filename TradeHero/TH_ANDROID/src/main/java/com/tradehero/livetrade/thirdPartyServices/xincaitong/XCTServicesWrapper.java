package com.tradehero.livetrade.thirdPartyServices.xincaitong;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.tradehero.livetrade.data.LiveTradeBalanceDTO;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustCancelDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustEnterDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePendingEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePositionDTO;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeServices;
import com.tradehero.livetrade.thirdPartyServices.haitong.HaitongUtils;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.APKDownloadNInstaller;
import com.tradehero.th.utils.DaggerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
@Singleton
public class XCTServicesWrapper implements LiveTradeServices {

    private static final String APK_URL = "http://fhmainstorage.blob.core.windows.net/fhres/XCT_QMGS_EXCH.apk";

    // 第三方证券与交易模块间的接口变量定义
    // ***************************************************************************************

    public final static String PACKAGE_NAME = "xct.exchangestock.qmgs";
    public final static String ACTIVITY_NAME = "lthj.exchangestock.FlashGridActv";
    // 定义交易Intent的Action
    // Intent参数
    // 命令标识
    public final static String INTENT_EXTRA_CMD = "action_cmd";
    // Intent命令类型
    // 启动交易(可以有参数,param1(股票市场),param2(股票代码),若有参数则跳转到股票的买入或卖出界面
    public final static String CMD_RUN_TRADE = "cmd_run_trade";
    // 证券传给交易的其他参数
    public final static String INTENT_EXTRA_PARAM_STANDARD = "action_param_standard";
    // 证券端交易界面标识

    public final String INTENT_EXTRA_PARAM_SERIliZABLE_TAG = "plugBackSerializable";

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;

    @Inject public XCTServicesWrapper()
    {
        DaggerUtils.inject(this);
    }

    @Override
    public boolean needCheckPhoneNumber() {
        return true;
    }

    @Override
    public boolean isSessionValid() {
        return false;
    }

    @Override
    public void login(Activity activity, String account, String password, LiveTradeCallback<LiveTradeSessionDTO> callback) {
        UserProfileDTO profileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (profileDTO.phoneNumber != null) {
            launchXCT(activity, profileDTO.phoneNumber);
        }
    }

    @Override
    public void logout() {

    }

    @Override
    public void signup(Activity activity) {
        HaitongUtils.openAnAccount(activity);
    }

    @Override
    public void getBalance(LiveTradeCallback<LiveTradeBalanceDTO> callback) {

    }

    @Override
    public void getPosition(LiveTradeCallback<LiveTradePositionDTO> callback) {

    }

    @Override
    public void buy(String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {

    }

    @Override
    public void sell(String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {

    }

    @Override
    public void pendingEntrustQuery(LiveTradeCallback<LiveTradePendingEntrustQueryDTO> callback) {

    }

    @Override
    public void entrustQuery(LiveTradeCallback<LiveTradeEntrustQueryDTO> callback) {

    }

    @Override
    public void entrustCancel(String marketCode, String entrustNo, String entrustDate, String withdrawCate, String securityId, LiveTradeCallback<LiveTradeEntrustCancelDTO> callback) {

    }

    @Override
    public void dealQuery(LiveTradeCallback<LiveTradeDealQueryDTO> callback) {

    }

    public void launchXCT(Activity activity, String phoneNum) {
        PackageManager packageManager = activity.getPackageManager();
        try {
            packageManager.getApplicationInfo(PACKAGE_NAME, 0);
            entrustPosition(activity, phoneNum);
        } catch (PackageManager.NameNotFoundException e) {
            APKDownloadNInstaller installer = new APKDownloadNInstaller();
            installer.downloadApk(activity, APK_URL, "全民股神交易");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void entrustPosition(Activity activity, String phoneNum) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName(PACKAGE_NAME, ACTIVITY_NAME);
		/*
		 * 判断交易进程是否存在,如果进程存在直接调起后台栈，如果不存在再重新创建
		 */
        boolean isProcessExist = isProcessExist(PACKAGE_NAME,activity);
        if(!isProcessExist){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else{
            //该flag,标识如果后台栈存在该进程,则直接调起
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setComponent(comp);
        intent.putExtra(INTENT_EXTRA_CMD, CMD_RUN_TRADE);
        Bundle bundle = new Bundle();
        JSONObject jsonObject = genJSON(phoneNum); //生成手机号
        bundle.putString(INTENT_EXTRA_PARAM_STANDARD, jsonObject.toString());
        bundle.putSerializable(INTENT_EXTRA_PARAM_SERIliZABLE_TAG, "0");
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 判断当前进程是否存在
     * @param pkg  进程包名
     * @param context 上下文实例
     * @return
     */
    public boolean isProcessExist(String pkg,Context context){
        boolean isExist = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 获取系统中所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager
                .getRunningAppProcesses();
        // 获取当前activity所在的进程
        // 对系统中所有正在运行的进程进行迭代，如果进程名不是当前进程，则Kill掉
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            String processName = appProcessInfo.processName;
            if (processName.equals(pkg)) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    /**
     * 获取手机号
     * @return
     */
    private JSONObject genJSON(String phoneNum) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Mdn", phoneNum);
            jsonObject.put("invokeType", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
