package com.baidu.android.pushservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import timber.log.Timber;

/**
 * Created by tho on 4/23/2014.
 */
public class RegistrationReceiver extends BroadcastReceiver
{
    @Override public void onReceive(Context context, Intent intent)
    {
        // do nothing, since it is for China
        Timber.d("lyl RegistrationReceiver");
    }
}
