package com.tradehero.th.network.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface HomeService
{
    @GET("/AppHome/{userId}")
    Response getHomePageContent(@Path("userId") int userId, @Query("isBeta") Boolean isBeta);
}
