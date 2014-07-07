package com.tradehero.th.network.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface HomeService
{
    @GET("/AppHome/{userId}")
    Response getHomePageContent(@Path("userId") int userId);
}
