package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.bing.BingTranslationResult;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

interface TranslationServiceBingAsync
{
    @GET("/v2/Http.svc/Translate") void requestForTranslation(
            @Header("Authorization") String authorization,
            @Query("from") String from,
            @Query("to") String to,
            @Query("contentType") String contentType,
            @Query("text") String text,
            Callback<BingTranslationResult> callback);
}
