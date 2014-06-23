package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.TranslationTokenFactory;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.translation.DTOProcessorGetTranslationToken;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

@Singleton public class TranslationTokenServiceWrapper
{
    @NotNull private final TranslationTokenService translationTokenService;
    @NotNull private final TranslationTokenServiceAsync translationTokenServiceAsync;
    @NotNull private final TranslationTokenFactory translationTokenFactory;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenServiceWrapper(
            @NotNull TranslationTokenService translationTokenService,
            @NotNull TranslationTokenServiceAsync translationTokenServiceAsync,
            @NotNull TranslationTokenFactory translationTokenFactory)
    {
        this.translationTokenService = translationTokenService;
        this.translationTokenServiceAsync = translationTokenServiceAsync;
        this.translationTokenFactory = translationTokenFactory;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<TranslationToken> createDTOProcessorGetTranslationToken()
    {
        return new DTOProcessorGetTranslationToken(translationTokenFactory);
    }
    //</editor-fold>

    //<editor-fold desc="Get Token">
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
    //</editor-fold>
}
