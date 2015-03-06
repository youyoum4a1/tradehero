package com.tradehero.th.network.service;

import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class YahooNewsServiceWrapper
{
    @NotNull private final YahooNewsService yahooNewsService;

    @Inject public YahooNewsServiceWrapper(
            @NotNull YahooNewsService yahooNewsService)
    {
        super();
        this.yahooNewsService = yahooNewsService;
    }

    public Response getNews(@NotNull String yahooSymbol)
    {
        return yahooNewsService.getNews(yahooSymbol);
    }

}
