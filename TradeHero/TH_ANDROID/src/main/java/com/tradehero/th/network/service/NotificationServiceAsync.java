package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.PaginatedNotificationDTO;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.Map;

interface NotificationServiceAsync
{
    @GET("/notifications")
    void getNotifications(
            @QueryMap Map<String, Object> options,
            Callback<PaginatedNotificationDTO> callback);

    @GET("/notifications/{pushId}")
    void getNotificationDetail(
            @Path("pushId") int pushId,
            Callback<NotificationDTO> callback);

    @POST("/notifications/read/{readPushId}")
    void markAsRead(
            @Path("readPushId") int readPushId,
            Callback<Response> callback);

    @POST("/notifications/read/-1")
    void markAsReadAll(Callback<Response> callback);

    @DELETE("/notifications/{pushId}")
    void deleteNotification(
            @Path("pushId") int pushId,
            Callback<String> callback);
}
