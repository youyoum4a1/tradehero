package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import retrofit.http.GET;
import rx.Observable;

public interface TranslationTokenServiceRx
{
    @GET("/translations/token/")
    Observable<TranslationToken> requestToken();
}
