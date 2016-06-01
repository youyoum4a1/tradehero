package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.tradehero.common.rx.PairGetSecond;
import com.ayondo.academy.api.translation.TranslationResult;
import com.ayondo.academy.api.translation.TranslationToken;
import com.ayondo.academy.api.translation.bing.BingTranslationToken;
import com.ayondo.academy.persistence.translation.TranslationTokenCacheRx;
import com.ayondo.academy.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class TranslationServiceWrapper
{
    @NonNull private final TranslationTokenCacheRx translationTokenCache;
    @NonNull private final TranslationServiceBingWrapper translationServiceBingWrapper;

    //<editor-fold desc="Constructors">
    @Inject public TranslationServiceWrapper(
            @NonNull TranslationTokenCacheRx translationTokenCache,
            @NonNull TranslationServiceBingWrapper translationServiceBingWrapper)
    {
        this.translationTokenCache = translationTokenCache;
        this.translationServiceBingWrapper = translationServiceBingWrapper;
    }
    //</editor-fold>

    @NonNull public Observable<TranslationResult> translateRx(final String from, final String to, final String text)
    {
        return translationTokenCache.get(new TranslationTokenKey())
                .map(new PairGetSecond<TranslationTokenKey, TranslationToken>())
                .flatMap(new Func1<TranslationToken, Observable<? extends TranslationResult>>()
                {
                    @Override public Observable<? extends TranslationResult> call(TranslationToken token)
                    {
                        if (token instanceof BingTranslationToken)
                        {
                            return translationServiceBingWrapper.translateRx(
                                    (BingTranslationToken) token,
                                    from, to, text);
                        }
                        throw new IllegalArgumentException("Unhandled TranslationToken " + token);
                    }
                });
    }
}
