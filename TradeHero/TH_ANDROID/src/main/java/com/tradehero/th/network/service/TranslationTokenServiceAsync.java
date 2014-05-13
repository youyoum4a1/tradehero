package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import retrofit.Callback;
import retrofit.http.GET;

interface TranslationTokenServiceAsync
{
    @GET("/translation/token/") void requestToken(Callback<TranslationToken> callback);
}
