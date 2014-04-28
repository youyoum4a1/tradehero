package com.tradehero.th.models.push.handlers;

import android.content.Intent;

public interface PushNotificationHandler
{
    String getAction();

    boolean handle(Intent intent);
}
