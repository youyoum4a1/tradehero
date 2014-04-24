package com.tradehero.th.network.service;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Path;

interface NotificationServiceAsync
{
    @POST("/notifications/read/{readPushId}")
    void markAsRead(@Path("readPushId") int readPushId, Callback<Response> callback);
}
