package com.ayondo.academy.network.service;

import com.ayondo.academy.api.translation.TranslationToken;
import retrofit.http.GET;
import rx.Observable;

public interface TranslationTokenServiceRx
{
    @GET("/translations/token/")
    Observable<TranslationToken> requestToken();
}
