package com.tradehero.th.network.service;

import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
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

    @GET("/rss/headline")
    Response getNews(
            @Query("s") String yahooSymbol)
            throws RetrofitError;
}
