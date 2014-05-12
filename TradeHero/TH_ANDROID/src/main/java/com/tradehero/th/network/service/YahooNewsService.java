package com.tradehero.th.network.service;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;


public interface YahooNewsService
{
    @GET("/rss/headline")
    void getNews(
            @Query("s") String yahooSymbol,
            Callback<Response> callback);

    @GET("/rss/headline")
    Response getNews(
            @Query("s") String yahooSymbol)
        throws RetrofitError;
}
