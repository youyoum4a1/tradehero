package com.tradehero.th.network.service;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by julien on 10/10/13
 */
public interface YahooNewsService
{
    @GET("/rss/headline")
    void getNews(
            @Query("s") String yahooSymbol,
            Callback<Response> callback);
}
