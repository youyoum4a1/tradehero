package com.tradehero.th.network.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

interface HomeServiceRx
{
    @GET("/AppHome/{userId}")
    Observable<Response> getHomePageContent(@Path("userId") int userId, @Query("isBeta") Boolean isBeta);
}
