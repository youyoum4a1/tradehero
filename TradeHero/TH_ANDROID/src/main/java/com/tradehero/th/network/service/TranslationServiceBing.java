package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.bing.BingTranslationResult;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

public interface TranslationServiceBing
{
    @GET("/v2/Http.svc/Translate") BingTranslationResult requestForTranslation(
            @Header("Authorization") String authorization,
            @Query("from") String from,
            @Query("to") String to,
            @Query("contentType") String contentType,
            @Query("text") String text);
}