package com.tradehero.th.base;

import com.tradehero.common.application.PApplication;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.push.IntentReceiver;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EmailSignUtils;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 3:33 PM Copyright (c) TradeHero */
public class Application extends PApplication
{
    public static final String TAG = Application.class.getSimpleName();

    @Override protected void init()
    {
        super.init();

        // Supposedly get the count of cores
        KnownExecutorServices.setCpuThreadCount(Runtime.getRuntime().availableProcessors());
        THLog.d(TAG, "Available Processors Count: " + KnownExecutorServices.getCpuThreadCount());

        DaggerUtils.initialize();

        THUser.initialize();
        EmailSignUtils.initialize();

        UAirship.takeOff(this);

        //use CustomPushNotificationBuilder to specify a custom layout
        CustomPushNotificationBuilder nb = new CustomPushNotificationBuilder();

        nb.statusBarIconDrawableId = R.drawable.superman_facebook;//custom status bar icon

        nb.layout = R.layout.notification;
        nb.layoutIconDrawableId = R.drawable.icon;//custom layout icon
        nb.layoutIconId = R.id.icon;
        nb.layoutSubjectId = R.id.subject;
        nb.layoutMessageId = R.id.message;

        // customize the sound played when a push is received
        //nb.soundUri = Uri.parse("android.resource://"+this.getPackageName()+"/" +R.raw.cat);

        PushManager.shared().setNotificationBuilder(nb);
        PushManager.shared().setIntentReceiver(IntentReceiver.class);

        THLog.showDeveloperKeyHash();
    }
}
