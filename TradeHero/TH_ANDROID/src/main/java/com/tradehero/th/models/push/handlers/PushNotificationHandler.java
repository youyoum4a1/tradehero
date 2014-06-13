package com.tradehero.th.models.push.handlers;

import android.content.Intent;
import com.tradehero.th.models.push.PushConstants;

public interface PushNotificationHandler
{
    PushConstants.THAction getAction();

    boolean handle(Intent intent);
}
