package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.client.Response;
import rx.Observable;

@Singleton public class YahooNewsServiceWrapper
{
    @NonNull private final YahooNewsServiceRx yahooNewsServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public YahooNewsServiceWrapper(@NonNull YahooNewsServiceRx yahooNewsServiceRx)
    {
        super();
        this.yahooNewsServiceRx = yahooNewsServiceRx;
    }
    //</editor-fold>

    @NonNull public Observable<Response> getNewsRx(@NonNull String yahooSymbol)
    {
        return yahooNewsServiceRx.getNews(yahooSymbol);
    }
}
