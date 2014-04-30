package com.tradehero.th.models.push.urbanairship;

import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.models.push.PushNotificationManager;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class UrbanAirshipPushNotificationManager implements PushNotificationManager
{
    @Inject public UrbanAirshipPushNotificationManager()
    {
    }

    @Override public void initialise()
    {
        UAirship.takeOff(Application.context());
        PushManager.enablePush();

        { // DEBUG
            String apid = PushManager.shared().getAPID();
            Timber.d("My Application onCreate - App APID: %s", apid);
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
        PushManager.shared().setIntentReceiver(UrbanAirshipIntentReceiver.class);
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
