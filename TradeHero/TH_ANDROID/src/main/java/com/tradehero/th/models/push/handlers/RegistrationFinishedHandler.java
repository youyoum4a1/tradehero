package com.tradehero.th.models.push.handlers;

import android.content.Context;
import android.content.Intent;
import com.tradehero.th.models.push.PushConstants;
import javax.inject.Inject;
import timber.log.Timber;

//import com.urbanairship.UAirship;
//import com.urbanairship.push.PushManager;

public class RegistrationFinishedHandler implements PushNotificationHandler
{
    public static String APID_UPDATED_ACTION_SUFFIX = ".apid.updated";
    private final Context context;

    @Inject public RegistrationFinishedHandler(Context context)
    {
        this.context = context;
    }

    @Override public PushConstants.THAction getAction()
    {
        return PushConstants.THAction.RegistrationFinished;
    }

    @Override public boolean handle(Intent intent)
    {
        Timber.i("Registration complete. APID: %s. Valid: %b",
                intent.getStringExtra("com.urbanairship.push.APID"),
                intent.getBooleanExtra("com.urbanairship.push.REGISTRATION_VALID", false));

        Intent launch = new Intent("com.urbanairship" + APID_UPDATED_ACTION_SUFFIX);
        context.sendBroadcast(launch);
        return true;
    }
}
