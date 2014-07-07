package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class TranslationTokenServiceWrapper
{
    @NotNull private final TranslationTokenService translationTokenService;
    @NotNull private final TranslationTokenServiceAsync translationTokenServiceAsync;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenServiceWrapper(
            @NotNull TranslationTokenService translationTokenService,
            @NotNull TranslationTokenServiceAsync translationTokenServiceAsync)
    {
        this.translationTokenService = translationTokenService;
        this.translationTokenServiceAsync = translationTokenServiceAsync;
    }
    //</editor-fold>

    //<editor-fold desc="Get Token">
    @NotNull public TranslationToken getToken()
    {
        return translationTokenService.requestToken();
    }

    @NotNull public MiddleCallback<TranslationToken> getToken(@Nullable Callback<TranslationToken> callback)
    {
        MiddleCallback<TranslationToken> middleCallback = new BaseMiddleCallback<>(callback);
        translationTokenServiceAsync.requestToken(middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
