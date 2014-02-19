package com.tradehero.th.base;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.application.PApplication;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EmailSignUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 3:33 PM Copyright (c) TradeHero */
public class Application extends PApplication
{
    public static final String TAG = Application.class.getSimpleName();

    @Inject protected PushNotificationManager pushNotificationManager;

    @Override protected void init()
    {
        super.init();

        // Supposedly get the count of cores
        KnownExecutorServices.setCpuThreadCount(Runtime.getRuntime().availableProcessors());
        THLog.d(TAG, "Available Processors Count: " + KnownExecutorServices.getCpuThreadCount());

        DaggerUtils.initialize(this);
        DaggerUtils.inject(this);

        THUser.initialize();

        EmailSignUtils.initialize();

        pushNotificationManager.initialise();

        THLog.showDeveloperKeyHash();
    }

    public void restartActivity(Class<? extends Activity> activityClass)
    {
        Intent newApp = new Intent(this, activityClass);
        newApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newApp);

        DaggerUtils.initialize(this);
        DaggerUtils.inject(this);
    }
}
