package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import java.util.Map;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface NotificationService
{
    @GET("/notifications")
    PaginatedNotificationDTO getNotifications(@QueryMap Map<String, Object> options);

    @GET("/notifications/{pushId}")
    NotificationDTO getNotificationDetail(@Path("pushId") int pushId);

    @POST("/notifications/read/{readPushId}")
    Response markAsRead(@Path("readPushId") int readPushId);
}
