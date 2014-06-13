package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import retrofit.http.GET;

public interface TranslationTokenService
{
    @GET("/translations/token/") TranslationToken requestToken();
}
