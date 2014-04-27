package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton
public class NotificationServiceWrapper
{
    private final NotificationService notificationService;
    private final NotificationServiceAsync notificationServiceAsync;

    @Inject public NotificationServiceWrapper(
            NotificationService notificationService,
            NotificationServiceAsync notificationServiceAsync)
    {
        this.notificationService = notificationService;
        this.notificationServiceAsync = notificationServiceAsync;
    }

    //<editor-fold desc="Get Notification Detail">
    public NotificationDTO getNotificationDetail(NotificationKey pushKey)
    {
        return notificationService.getNotificationDetail(pushKey.key);
    }

    public MiddleCallback<NotificationDTO> getNotificationDetail(NotificationKey pushKey, Callback<NotificationDTO> callback)
    {
        MiddleCallback<NotificationDTO> middleCallback = new MiddleCallback<>(callback);
        notificationServiceAsync.getNotificationDetail(pushKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Mark As Read">
    public Response markAsRead(NotificationKey pushKey)
    {
        return notificationService.markAsRead(pushKey.key);
    }

    public MiddleCallback<Response> markAsRead(NotificationKey pushKey, Callback<Response> callback)
    {
        MiddleCallback<Response> readMiddleCallback = new MiddleCallback<>(callback);
        notificationServiceAsync.markAsRead(pushKey.key, readMiddleCallback);
        return readMiddleCallback;
    }
    //</editor-fold>
}
