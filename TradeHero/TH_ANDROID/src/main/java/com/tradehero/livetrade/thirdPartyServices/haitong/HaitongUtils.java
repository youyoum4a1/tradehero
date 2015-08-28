package com.tradehero.livetrade.thirdPartyServices.haitong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tradehero.th.R;
import com.tradehero.th.activities.TradeHeroMainActivity;

import cn.htsec.TradeModule;
import cn.htsec.data.pkg.trade.TradeManager;

/**
 * Created by palmer on 15/7/18.
 */
public class HaitongUtils {

    //到海通登陆界面
    public final static void jumpToLoginHAITONG(Activity activity){
        if(activity == null){
            return;
        }
        Intent intent = new Intent(activity, TradeModule.class);
        Bundle bundle = new Bundle();
        //用户唯一标识
        bundle.putString(TradeModule.EXTRA_KEY_USERID, "");
        //渠道
        bundle.putString(TradeModule.EXTRA_KEY_CHANNEL, "ht6c4bb1384e63c92d");
        //设置在线时间(秒)
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, TradeHeroMainActivity.ACTIVITY_RESULT_HAITONG_TRADE);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    //到海通开户界面
    public final static void openAnAccount(Activity activity){
        if(activity == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("type", 0);//开户 ，开户传此
		intent.putExtra("channel", "mowhse");// 开户时可以传此参数
//		intent.putExtra("mobileNo", "手机号");// 开户时可以传此参数，有就传 没有不传
        intent.setClass(activity, com.cairh.app.sjkh.MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    public final static void bankTransfer(Activity activity){
        if(activity == null){
            return;
        }
        if(TradeManager.getInstance(activity).isLogined()) {
            Intent intent = new Intent(activity, TradeModule.class);
            Bundle bundle = new Bundle();
            //银证转账界面
            bundle.putString(TradeModule.EXTRA_KEY_PAGETYPE, TradeModule.PAGETYPE_TRANSFER);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        } else {
            jumpToLoginHAITONG(activity);
        }
    }

    public static String getMarketCodeBySymbol(String symbol){
        if(TextUtils.isEmpty(symbol)){
            return "-1";
        }
        if(symbol.startsWith("420")){
            return "6";
        }
        if(symbol.startsWith("400")){
            return "5";
        }
        if(symbol.startsWith("9")){
            return "2";
        }
        if(symbol.startsWith("2")){
            return "3";
        }
        if(symbol.startsWith("6")){
            return "0";
        }
        if(symbol.startsWith("0")){
            return "1";
        }
        return "7";
    }
}
