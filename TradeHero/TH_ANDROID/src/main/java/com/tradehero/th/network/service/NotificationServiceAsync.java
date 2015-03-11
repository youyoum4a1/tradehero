package com.tradehero.th.network.service;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.Path;

interface NotificationServiceAsync
{

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

    @DELETE("/notifications/-1")
    void deleteAllNotifications(
            Callback<String> callback);
}
