package com.ayondo.academy.models.push.handlers;

import android.content.Intent;
import com.ayondo.academy.models.push.PushConstants;

public interface PushNotificationHandler
{
    PushConstants.THAction getAction();

    boolean handle(Intent intent);
}
