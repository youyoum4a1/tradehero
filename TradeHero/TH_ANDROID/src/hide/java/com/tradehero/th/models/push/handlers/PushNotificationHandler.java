package com.androidth.general.models.push.handlers;

import android.content.Intent;
import com.androidth.general.models.push.PushConstants;

public interface PushNotificationHandler
{
    PushConstants.THAction getAction();

    boolean handle(Intent intent);
}
