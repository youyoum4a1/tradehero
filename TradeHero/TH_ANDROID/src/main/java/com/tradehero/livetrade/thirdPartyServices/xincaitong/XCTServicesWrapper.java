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

    private static final String APK_URL = "http://fhmainstorage.blob.core.windows.net/fhres/cps_20150805.apk";

    // 第三方证券与交易模块间的接口变量定义
    // ***************************************************************************************
    // 定义交易Intent的Action
    public final static String TRADE_ACTION = "com.lthjxct.android.appstock.stock.2nd.cps.caopanshou";

    // 定义证券Intent的Action
    public final static String STOCK_ACTION = "com.lthjxct.android.appstock.stock.2nd.cps.demo";

    // Intent参数
    // 命令标识
    public final static String INTENT_EXTRA_CMD = "action_cmd";
    // Intent命令类型
    // 启动交易(可以有参数,param1(股票市场),param2(股票代码),若有参数则跳转到股票的买入或卖出界面
    public final static String CMD_RUN_TRADE = "cmd_run_trade";
    // 停止交易,无参数
    public final static String CMD_STOP_TRADE = "cmd_stop_trade";
    // 交易调证券,进入该股票的行情界面，param1(股票市场),param2(股票代码)
    public final static String CMD_RUN_STOCK = "cmd_run_stock";
    // 添加自选股,param1(股票市场),param2(股票代码)
    public final static String CMD_ADD_STOCK = "cmd_add_stock";
    // 市场类型命令参数,使用Bundle保存，股票市场定义(1:上海，2: 深圳, 3: 香港)
    public final static String INTENT_EXTRA_PARAM_MARKET = "action_param_market";
    // 股票代码命令参数,使用Bundle保存
    public final static String INTENT_EXTRA_PARAM_STOCKCODE = "action_param_stockcode";
    // 委托买卖方向(0正常登录，1登录后进入买，2登录后进入卖)
    public final static String INTENT_EXTRA_PARAM_DIRECTION = "action_param_direction";
    // 证券传给交易的其他参数
    public final static String INTENT_EXTRA_PARAM_STANDARD = "action_param_standard";
    // 证券端交易界面标识
    public final static String INTENT_EXTRA_PARAM_UIID = "action_param_uiid";
    // 联网类型参数
    public final static String INTENT_EXTRA_PARAM_NET = "action_param_net";
    // 证券端的action参数，用bundle保存
    public final static String INTENT_TRADE_ACTION = "action";
    public final static String PAKCAGER_NAME = "pakagerName";
    public final static String THIRDAPPPAKCAGER_NAME = "com.tradehero.th";

    public final String INTENT_EXTRA_PARAM_PARCELABLE_TAG = "plugBackParcelable";
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
            packageManager.getApplicationInfo("lthj.exchangestock.caopanshou", 0);
            entrustPosition(activity, phoneNum);
        } catch (PackageManager.NameNotFoundException e) {
            APKDownloadNInstaller installer = new APKDownloadNInstaller();
            installer.downloadApk(activity, APK_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void entrustPosition(Activity activity, String phoneNum) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName(
                "lthj.exchangestock.caopanshou",
                "lthj.exchangestock.FlashGridActv");
		/*
		 * 判断交易进程是否存在,如果进程存在直接调起后台栈，如果不存在再重新创建
		 */
        boolean isProcessExist = isProcessExist("lthj.exchangestock.caopanshou", activity);
        if(!isProcessExist){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else{
            //该flag,标识如果后台栈存在该进程,则直接调起
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        intent.setComponent(comp);
        intent.setAction("com.lthjxct.android.appstock.stock.2nd.caopanshou");
        intent.putExtra(INTENT_EXTRA_CMD, CMD_RUN_TRADE);
        Bundle bundle = new Bundle();
        JSONObject jsonObject = genJSON(phoneNum);
        bundle.putString(INTENT_EXTRA_PARAM_STANDARD, jsonObject.toString());
        bundle.putSerializable(INTENT_EXTRA_PARAM_SERIliZABLE_TAG, "0");
        bundle.putString(INTENT_EXTRA_PARAM_NET, "");
        bundle.putString(INTENT_TRADE_ACTION, STOCK_ACTION);
        bundle.putString(PAKCAGER_NAME, THIRDAPPPAKCAGER_NAME);
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
