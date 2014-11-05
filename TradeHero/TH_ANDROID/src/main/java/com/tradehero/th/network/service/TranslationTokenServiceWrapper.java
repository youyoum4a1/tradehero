package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class TranslationTokenServiceWrapper
{
    @NonNull private final TranslationTokenService translationTokenService;
    @NonNull private final TranslationTokenServiceAsync translationTokenServiceAsync;
    @NonNull private final TranslationTokenServiceRx translationTokenServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenServiceWrapper(
            @NonNull TranslationTokenService translationTokenService,
            @NonNull TranslationTokenServiceAsync translationTokenServiceAsync,
            @NonNull TranslationTokenServiceRx translationTokenServiceRx)
    {
        this.translationTokenService = translationTokenService;
        this.translationTokenServiceAsync = translationTokenServiceAsync;
        this.translationTokenServiceRx = translationTokenServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Token">
    @NonNull public TranslationToken getToken()
    {
        return translationTokenService.requestToken();
    }

    @NonNull public MiddleCallback<TranslationToken> getToken(@Nullable Callback<TranslationToken> callback)
    {
        MiddleCallback<TranslationToken> middleCallback = new BaseMiddleCallback<>(callback);
        translationTokenServiceAsync.requestToken(middleCallback);
        return middleCallback;
    }

    @NonNull public Observable<TranslationToken> getTokenRx()
    {
        return translationTokenServiceRx.requestToken();
    }
    //</editor-fold>
}
