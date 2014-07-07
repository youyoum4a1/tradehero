package com.tradehero.th.network.service;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface HomeServiceAsync
{
    @GET("/AppHome/{userId}")
    void getHomePageContent(@Path("userId") int userId, Callback<Response> callback);
}
