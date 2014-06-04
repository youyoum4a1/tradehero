package com.tradehero.th.models.push;

import android.app.Notification;

public interface THNotificationBuilder
{
    Notification buildNotification(String message, int notificationId);

    int getNotifyId(int notificationId);
}
