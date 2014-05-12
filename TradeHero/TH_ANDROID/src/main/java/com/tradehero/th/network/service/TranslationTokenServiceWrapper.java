package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.TranslationTokenFactory;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.translation.DTOProcessorGetTranslationToken;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class TranslationTokenServiceWrapper
{
    private TranslationTokenService translationTokenService;
    private TranslationTokenServiceAsync translationTokenServiceAsync;
    private TranslationTokenFactory translationTokenFactory;

    @Inject public TranslationTokenServiceWrapper(
            TranslationTokenService translationTokenService,
            TranslationTokenServiceAsync translationTokenServiceAsync,
            TranslationTokenFactory translationTokenFactory)
    {
        this.translationTokenService = translationTokenService;
        this.translationTokenServiceAsync = translationTokenServiceAsync;
        this.translationTokenFactory = translationTokenFactory;
    }

    protected DTOProcessor<TranslationToken> createDTOProcessorGetTranslationToken()
    {
        return new DTOProcessorGetTranslationToken(translationTokenFactory);
    }

    public TranslationToken getToken()
    {
        return createDTOProcessorGetTranslationToken().process(translationTokenService.requestToken());
    }

    public MiddleCallback<TranslationToken> getToken(Callback<TranslationToken> callback)
    {
        MiddleCallback<TranslationToken> middleCallback = new BaseMiddleCallback<>(
                callback,
                createDTOProcessorGetTranslationToken());
        translationTokenServiceAsync.requestToken(middleCallback);
        return middleCallback;
    }
}
