package com.tradehero.th.utils;

import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.push.IntentReceiver;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;

/** Created with IntelliJ IDEA. User: tho Date: 12/2/13 Time: 5:40 PM Copyright (c) TradeHero */
public class PushUtils
{
    public static void initialize()
    {
        UAirship.takeOff(Application.context());
        PushManager.enablePush();

        //use CustomPushNotificationBuilder to specify a custom layout
        CustomPushNotificationBuilder nb = new CustomPushNotificationBuilder();

        nb.statusBarIconDrawableId = R.drawable.notification_status_icon;

        nb.layout = R.layout.notification;
        nb.layoutIconDrawableId = R.drawable.notification_logo;
        nb.layoutIconId = R.id.notification_icon;
        nb.layoutSubjectId = R.id.notification_subject;
        nb.layoutMessageId = R.id.message;

        // customize the sound played when a push is received
        //nb.soundUri = Uri.parse("android.resource://"+this.getPackageName()+"/" +R.raw.cat);

        PushManager.shared().setNotificationBuilder(nb);
        PushManager.shared().setIntentReceiver(IntentReceiver.class);
    }
}
