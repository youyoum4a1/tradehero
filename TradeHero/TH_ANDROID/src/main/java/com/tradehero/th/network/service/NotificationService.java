package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

import java.util.Map;

public interface NotificationService
{
    @GET("/notifications")
    PaginatedNotificationDTO getNotifications(@QueryMap Map<String, Object> options);

    @GET("/notifications/{pushId}")
    NotificationDTO getNotificationDetail(@Path("pushId") int pushId);
}
