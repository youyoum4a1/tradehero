package com.tradehero.th.models.push;

public class PushConstants
{
    public static final String KEY_PUSH_ID = "i";

    public static final String KEY_NOTIFICATION_ID = PushConstants.class.getName() + ".notificationId";
    public static final String KEY_NOTIFICATION_CONTENT = PushConstants.class.getName() + ".notificationContent";
    public static final String KEY_PUSH_GROUP_ID = PushConstants.class.getName() + ".notificationGroupId";

    public static final String ACTION_NOTIFICATION_CLICKED = "com.tradehero.th.ACTION_NOTIFICATION_CLICKED";
    public static final String ACTION_MESSAGE_RECEIVED = "com.tradehero.th.ACTION_MESSAGE_RECEIVED";

    public static enum THAction
    {
        Opened, Received, RegistrationFinished, GcmDeleted
    }
}
