package com.androidth.general.network.service;

import com.androidth.general.api.translation.TranslationToken;
import retrofit2.http.GET;
import rx.Observable;

public interface TranslationTokenServiceRx
{
    @GET("api/translations/token/")
    Observable<TranslationToken> requestToken();
}
