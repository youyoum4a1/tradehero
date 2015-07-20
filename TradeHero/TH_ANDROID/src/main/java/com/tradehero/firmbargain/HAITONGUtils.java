package com.tradehero.firmbargain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cairh.app.sjkh.MainActivity;
import com.tradehero.th.R;
import com.tradehero.th.activities.TradeHeroMainActivity;

import cn.htsec.TradeModule;

/**
 * Created by palmer on 15/7/18.
 */
public class HAITONGUtils {

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
        bundle.putString(TradeModule.EXTRA_KEY_CHANNEL, "htbab81aca544e305a");
        //设置在线时间(秒)
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, TradeHeroMainActivity.ACTIVITY_RESULT_HAITONG_TRADE);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        return;
    }

    //到海通开户界面
    public final static void openAnAccount(Activity activity){
        Intent intent = new Intent();
        intent.putExtra("type", 0);//启动的功能编号,具体详见功能编号表
        intent.putExtra("username", "username");//自动登陆的账号,非必填
        intent.putExtra("password", "password");//自动登陆的密码
        intent.setClass(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }
}
