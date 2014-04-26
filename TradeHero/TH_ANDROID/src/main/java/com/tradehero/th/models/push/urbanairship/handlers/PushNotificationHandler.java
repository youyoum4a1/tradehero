package com.tradehero.th.models.push.urbanairship.handlers;

import android.content.Intent;

/**
 * Created by thonguyen on 26/4/14.
 */
public interface PushNotificationHandler
{
    String getAction();

    boolean handle(Intent intent);
}
