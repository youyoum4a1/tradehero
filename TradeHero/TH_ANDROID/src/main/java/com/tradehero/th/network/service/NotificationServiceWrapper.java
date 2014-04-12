package com.tradehero.th.network.service;

import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by thonguyen on 12/4/14.
 */
@Singleton
public class NotificationServiceWrapper
{
    private final NotificationServiceAsync notificationServiceAsync;

    @Inject public NotificationServiceWrapper(
            NotificationServiceAsync notificationServiceAsync)
    {
        this.notificationServiceAsync = notificationServiceAsync;
    }

    public MiddleCallback<Response> markAsRead(int readPushId, Callback<Response> callback)
    {
        MiddleCallback<Response> readMiddleCallback = new MiddleCallback<>(callback);
        notificationServiceAsync.markAsRead(readPushId, readMiddleCallback);
        return readMiddleCallback;
    }
}
