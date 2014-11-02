package com.tradehero.th.network.service;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface YahooNewsServiceRx
{
    @GET("/rss/headline")
    Observable<Response> getNews(
            @Query("s") String yahooSymbol);
}
