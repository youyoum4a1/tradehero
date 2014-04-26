package com.tradehero.th.models.push.baidu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificaionReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        if (BaiduPushMessageReceiver.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction()))
        {
            BaiduPushMessageReceiver.handle(intent);
        }
    }
}
