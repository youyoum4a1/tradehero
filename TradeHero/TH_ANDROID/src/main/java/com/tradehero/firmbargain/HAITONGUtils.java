package com.tradehero.firmbargain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tradehero.th.activities.MainActivity;

import cn.htsec.TradeModule;

/**
 * Created by palmer on 15/7/18.
 */
public class HAITONGUtils {

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
        activity.startActivityForResult(intent, MainActivity.ACTIVITY_RESULT_HAITONG_TRADE);
        return;
    }
}
