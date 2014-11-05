package com.tradehero.th.network.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import retrofit.client.Response;
import rx.Observable;

@Singleton public class YahooNewsServiceWrapper
{
    @NonNull private final YahooNewsServiceRx yahooNewsServiceRx;

    @Inject public YahooNewsServiceWrapper(@NonNull YahooNewsServiceRx yahooNewsServiceRx)
    {
        super();
        this.yahooNewsServiceRx = yahooNewsServiceRx;
    }

    public Observable<Response> getNewsRx(@NonNull String yahooSymbol)
    {
        return yahooNewsServiceRx.getNews(yahooSymbol);
    }
}
