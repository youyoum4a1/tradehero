package com.ayondo.academy.models.push;

public class PushConstants
{
    public static final String KEY_PUSH_ID = "i";

    public static final String KEY_NOTIFICATION_ID = PushConstants.class.getName() + ".notificationId";
    public static final String KEY_NOTIFICATION_CONTENT = PushConstants.class.getName() + ".notificationContent";
    public static final String KEY_PUSH_GROUP_ID = PushConstants.class.getName() + ".notificationGroupId";

    public static final String ACTION_NOTIFICATION_CLICKED = "com.ayondo.academy.ACTION_NOTIFICATION_CLICKED";
    public static final String ACTION_MESSAGE_RECEIVED = "com.ayondo.academy.ACTION_MESSAGE_RECEIVED";

    public static enum THAction
    {
        Opened, Received, RegistrationFinished, GcmDeleted
    }

    public static enum PushProvider
    {
        URBAN_AIRSHIP,
        BAIDU,
    }
}
