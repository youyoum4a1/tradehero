package com.tradehero.th.network.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;
import rx.Observable;

@Singleton public class YahooNewsServiceWrapper
{
    @NotNull private final YahooNewsServiceRx yahooNewsServiceRx;

    @Inject public YahooNewsServiceWrapper(@NotNull YahooNewsServiceRx yahooNewsServiceRx)
    {
        super();
        this.yahooNewsServiceRx = yahooNewsServiceRx;
    }

    public Observable<Response> getNewsRx(@NotNull String yahooSymbol)
    {
        return yahooNewsServiceRx.getNews(yahooSymbol);
    }
}
