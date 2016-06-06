package com.androidth.general.network.service;

import com.androidth.general.api.translation.TranslationToken;
import retrofit.http.GET;
import rx.Observable;

public interface TranslationTokenServiceRx
{
    @GET("/translations/token/")
    Observable<TranslationToken> requestToken();
}
