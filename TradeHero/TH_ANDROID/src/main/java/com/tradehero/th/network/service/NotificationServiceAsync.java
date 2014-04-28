package com.tradehero.th.network.service;

import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import java.util.Map;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

interface NotificationServiceAsync
{
    @GET("/notifications")
    void getNotifications(
            @QueryMap Map<String, Object> options,
            Callback<PaginatedDTO<NotificationDTO>> callback);

    @GET("/notifications/{pushId}")
    void getNotificationDetail(
            @Path("pushId") int pushId,
            Callback<NotificationDTO> callback);

    @POST("/notifications/read/{readPushId}")
    void markAsRead(
            @Path("readPushId") int readPushId,
            Callback<Response> callback);
}
