package com.ayondo.academy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;

public class ActivityUtil
{
    private static final String UPGRADE_INTENT_ACTION_NAME = "com.ayondo.academy.upgrade.ALERT";
    private static final String RENEW_TOKEN_INTENT_ACTION_NAME = "com.ayondo.academy.auth.token.ALERT";

    @NonNull public static IntentFilter getIntentFilterUpgrade()
    {
        return new IntentFilter(UPGRADE_INTENT_ACTION_NAME);
    }

    @NonNull public static Intent getIntentUpgrade()
    {
        return new Intent(UPGRADE_INTENT_ACTION_NAME);
    }

    @NonNull public static IntentFilter getIntentFilterSocialToken()
    {
        return new IntentFilter(RENEW_TOKEN_INTENT_ACTION_NAME);
    }

    @NonNull public static Intent getIntentSocialToken()
    {
        return new Intent(RENEW_TOKEN_INTENT_ACTION_NAME);
    }

    public static void sendSupportEmail(@NonNull final Context context, @NonNull Intent emailIntent)
    {
        context.startActivity(Intent.createChooser(
                emailIntent,
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
}
