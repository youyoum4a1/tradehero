package com.tradehero.th.network.service;

import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

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

}
