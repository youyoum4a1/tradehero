package com.tradehero.th.network.service;

import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton public class YahooNewsServiceWrapper
{
    @NotNull private final YahooNewsService yahooNewsService;
    @NotNull private final YahooNewsServiceAsync yahooNewsServiceAsync;

    @Inject public YahooNewsServiceWrapper(
            @NotNull YahooNewsService yahooNewsService,
            @NotNull YahooNewsServiceAsync yahooNewsServiceAsync)
    {
        super();
        this.yahooNewsService = yahooNewsService;
        this.yahooNewsServiceAsync = yahooNewsServiceAsync;
    }

    public Response getNews(@NotNull String yahooSymbol)
    {
        return yahooNewsService.getNews(yahooSymbol);
    }

    public BaseMiddleCallback<Response> getNews(String yahooSymbol,
            Callback<Response> callback)
    {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        yahooNewsServiceAsync.getNews(yahooSymbol, middleCallback);
        return middleCallback;
    }
}
