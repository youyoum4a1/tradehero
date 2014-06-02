package com.tradehero.th.models.push;

public class PushConstants
{
    public static final String PUSH_ID_KEY = "i";

    public static final String KEY_NOTIFICATION_ID = PushConstants.class.getName() + ".notificationId";
    public static final String KEY_NOTIFICATION_CONTENT = PushConstants.class.getName() + ".notificationContent";

    public static final String ACTION_NOTIFICATION_CLICKED = "ACTION_NOTIFICATION_CLICKED";

    public static enum THAction
    {
        Opened, Received, RegistrationFinished, GcmDeleted

    }
}
