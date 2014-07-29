package com.tradehero.th.network.service;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface HomeServiceAsync
{
    @GET("/AppHome/{userId}")
    void getHomePageContent(@Path("userId") int userId, Callback<Response> callback, @Query("isBeta") Boolean isBeta);
}
