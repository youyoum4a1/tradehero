package com.tradehero.th.network.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface YahooNewsService
{
    @GET("/rss/headline")
    Response getNews(
            @Query("s") String yahooSymbol);
}
