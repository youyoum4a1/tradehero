package com.tradehero.th.utils;

import android.content.Context;
import android.content.Intent;
import com.tradehero.th.R;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ActivityUtil
{
    @Inject public ActivityUtil()
    {
    }

    public void sendSupportEmail(final Context context, Intent emailIntent)
    {
        context.startActivity(Intent.createChooser(
                emailIntent,
                context.getString(R.string.google_play_send_support_email_chooser_title)));
    }
}
