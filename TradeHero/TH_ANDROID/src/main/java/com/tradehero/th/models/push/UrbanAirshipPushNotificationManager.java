package com.tradehero.th.models.push;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.push.IntentReceiver;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/10/14.
 */
@Singleton public class UrbanAirshipPushNotificationManager implements PushNotificationManager
{
    public static final String TAG = UrbanAirshipPushNotificationManager.class.getSimpleName();

    @Inject public UrbanAirshipPushNotificationManager()
    {
    }

    @Override public void initialise()
    {
        UAirship.takeOff(Application.context());
        PushManager.enablePush();

        { // DEBUG
            String apid = PushManager.shared().getAPID();
            THLog.d(TAG, "My Application onCreate - App APID: " + apid);
        }

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

    @Override public void enablePush()
    {
        PushManager.enablePush();
    }

    @Override public void disablePush()
    {
        PushManager.disablePush();
    }

    @Override public void setSoundEnabled(boolean enabled)
    {
        PushManager.shared().getPreferences().setSoundEnabled(enabled);
    }

    @Override public void setVibrateEnabled(boolean enabled)
    {
        PushManager.shared().getPreferences().setVibrateEnabled(enabled);
    }
}
