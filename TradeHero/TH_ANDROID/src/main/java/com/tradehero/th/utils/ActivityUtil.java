package com.tradehero.th.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import javax.inject.Singleton;

@Singleton public class ActivityUtil
{
    public static void sendSupportEmail(@NonNull final Context context, @NonNull Intent emailIntent)
    {
        context.startActivity(Intent.createChooser(
                emailIntent,
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
}
