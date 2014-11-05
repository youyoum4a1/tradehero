package com.tradehero.th.network.service;

import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.translation.bing.BingTranslationResult;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.models.translation.bing.BaseMiddleCallbackBingTranslationResult;
import com.tradehero.th.network.retrofit.CallbackWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import retrofit.Callback;
import rx.Observable;

@Singleton public class TranslationServiceBingWrapper
{
    private static final String PREFERRED_CONTENT_TYPE = "text/plain";

    @NonNull private final TranslationServiceBing translationServiceBing;
    @NonNull private final TranslationServiceBingAsync translationServiceBingAsync;
    @NonNull private final TranslationServiceBingRx translationServiceBingRx;

    @Inject public TranslationServiceBingWrapper(
            @NonNull TranslationServiceBing translationServiceBing,
            @NonNull TranslationServiceBingAsync translationServiceBingAsync,
            @NonNull TranslationServiceBingRx translationServiceBingRx)
    {
        this.translationServiceBing = translationServiceBing;
        this.translationServiceBingAsync = translationServiceBingAsync;
        this.translationServiceBingRx = translationServiceBingRx;
    }

    public BingTranslationResult translate(BingTranslationToken token, String from, String to, String text)
    {
        return translationServiceBing.requestForTranslation(
                token.getPrefixedAccessToken(),
                from, to, PREFERRED_CONTENT_TYPE, text);
    }

    public CallbackWrapper<TranslationResult> translate(BingTranslationToken token, String from, String to, String text,
            Callback<TranslationResult> callback)
    {
        BaseMiddleCallbackBingTranslationResult middleCallback = new BaseMiddleCallbackBingTranslationResult(callback);
        translationServiceBingAsync.requestForTranslation(
                token.getPrefixedAccessToken(),
                from, to, PREFERRED_CONTENT_TYPE, text,
                middleCallback);
        return middleCallback;
    }

    public Observable<BingTranslationResult> translateRx(@NonNull BingTranslationToken token, String from, String to, String text)
    {
        return translationServiceBingRx.requestForTranslation(
                token.getPrefixedAccessToken(),
                from, to, PREFERRED_CONTENT_TYPE, text);
    }
}
