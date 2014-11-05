package com.tradehero.th.network.service;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.CallbackWrapper;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.translation.TranslationTokenCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import rx.Observable;
import timber.log.Timber;

@Singleton public class TranslationServiceWrapper
{
    @NonNull private final TranslationTokenCacheRx translationTokenCache;
    @NonNull private final TranslationTokenCacheRx translationTokenCacheRx;
    @NonNull private final TranslationServiceBingWrapper translationServiceBingWrapper;

    @Inject public TranslationServiceWrapper(
            @NonNull TranslationTokenCacheRx translationTokenCache,
            @NonNull TranslationTokenCacheRx translationTokenCacheRx,
            @NonNull TranslationServiceBingWrapper translationServiceBingWrapper)
    {
        this.translationTokenCache = translationTokenCache;
        this.translationTokenCacheRx = translationTokenCacheRx;
        this.translationServiceBingWrapper = translationServiceBingWrapper;
    }

    @Deprecated
    public TranslationResult translate(String from, String to, String text)
    {
        TranslationToken token = translationTokenCache.get(new TranslationTokenKey()).toBlocking().first().second;

        if (token instanceof BingTranslationToken)
        {
            return translationServiceBingWrapper.translate((BingTranslationToken) token,
                    from, to, text);
        }
        throw new IllegalArgumentException("Unhandled TranslationToken " + token);
    }

    public MiddleCallback<TranslationResult> translate(String from, String to, String text, Callback<TranslationResult> callback)
    {
        final MiddleCallback<TranslationResult> middleCallback = new BaseMiddleCallback<>(callback);
        TranslationTokenKey key = new TranslationTokenKey();
        translationTokenCache.get(key)
                .map(pair -> translate(pair.second, from, to, text, middleCallback));
        return middleCallback;
    }

    protected CallbackWrapper<TranslationResult> translate(TranslationToken token, String from, String to, String text, MiddleCallback<TranslationResult> callback)
    {
        if (token instanceof BingTranslationToken)
        {
            return translationServiceBingWrapper.translate((BingTranslationToken) token,
                    from, to, text, callback);
        }
        throw new IllegalArgumentException("Unhandled TranslationToken " + token);
    }

    protected class TranslationServiceWrapperTokenListener implements DTOCacheNew.Listener<TranslationTokenKey, TranslationToken>
    {
        private final String from;
        private final String to;
        private final String text;
        private final MiddleCallback<TranslationResult> middleCallback;

        public TranslationServiceWrapperTokenListener(String from, String to, String text,
                MiddleCallback<TranslationResult> middleCallback)
        {
            this.from = from;
            this.to = to;
            this.text = text;
            this.middleCallback = middleCallback;
        }

        @Override public void onDTOReceived(@NonNull TranslationTokenKey key, @NonNull TranslationToken value)
        {
            translate(value, from, to, text, middleCallback);
        }

        @Override public void onErrorThrown(@NonNull TranslationTokenKey key, @NonNull Throwable error)
        {
            if (middleCallback != null && error instanceof RetrofitError)
            {
                middleCallback.failure((RetrofitError) error);
            }
            else
            {
                Timber.e(error, "");
            }
        }
    }

    public Observable<TranslationResult> translateRx(String from, String to, String text)
    {
        return translationTokenCacheRx.get(new TranslationTokenKey())
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
