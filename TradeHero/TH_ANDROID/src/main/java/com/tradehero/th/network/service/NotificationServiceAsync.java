package com.tradehero.th.network.service;

import com.facebook.Response;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

interface NotificationServiceAsync
{
    @GET("/notifications/read/{readPushId}")
    void markAsRead(@Path("readPushId") int readPushId, Callback<Response> callback);
}
