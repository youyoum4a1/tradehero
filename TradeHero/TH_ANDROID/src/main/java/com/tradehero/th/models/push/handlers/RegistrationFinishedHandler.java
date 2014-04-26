package com.tradehero.th.models.push.handlers;

import android.content.Context;
import android.content.Intent;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by thonguyen on 26/4/14.
 */
public class RegistrationFinishedHandler implements PushNotificationHandler
{
    public static String APID_UPDATED_ACTION_SUFFIX = ".apid.updated";
    private final Context context;

    @Inject public RegistrationFinishedHandler(Context context)
    {
        this.context = context;
    }

    @Override public String getAction()
    {
        return PushManager.ACTION_REGISTRATION_FINISHED;
    }

    @Override public boolean handle(Intent intent)
    {
        Timber.i("Registration complete. APID: %s. Valid: %b",
                intent.getStringExtra(PushManager.EXTRA_APID),
                intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));

        Intent launch = new Intent(UAirship.getPackageName() + APID_UPDATED_ACTION_SUFFIX);
        context.sendBroadcast(launch);
        return true;
    }
}
