package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.persistence.translation.TranslationTokenCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class TranslationServiceWrapper
{
    @NonNull private final TranslationTokenCacheRx translationTokenCache;
    @NonNull private final TranslationServiceBingWrapper translationServiceBingWrapper;

    @Inject public TranslationServiceWrapper(
            @NonNull TranslationTokenCacheRx translationTokenCache,
            @NonNull TranslationServiceBingWrapper translationServiceBingWrapper)
    {
        this.translationTokenCache = translationTokenCache;
        this.translationServiceBingWrapper = translationServiceBingWrapper;
    }

    public Observable<TranslationResult> translateRx(String from, String to, String text)
    {
        return translationTokenCache.get(new TranslationTokenKey())
                .map(pair -> pair.second)
                .flatMap(token -> {
                    if (token instanceof BingTranslationToken)
                    {
                        return translationServiceBingWrapper.translateRx((BingTranslationToken) token,
                                from, to, text);
                    }
                    throw new IllegalArgumentException("Unhandled TranslationToken " + token);
                });
    }

}
