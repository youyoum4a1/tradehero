package com.tradehero.th.utils;

import android.content.Context;
import android.content.Intent;
import com.tradehero.th.R;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 11/29/13 Time: 11:59 AM To change this template use File | Settings | File Templates. */
@Singleton public class ActivityUtil
{
    public static final String TAG = ActivityUtil.class.getSimpleName();

    @Inject public ActivityUtil()
    {
    }

    public void sendSupportEmail(final Context context, Intent emailIntent)
    {
        context.startActivity(Intent.createChooser(
                emailIntent,
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
}
