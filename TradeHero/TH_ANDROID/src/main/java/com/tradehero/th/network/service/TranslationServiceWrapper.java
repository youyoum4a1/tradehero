package com.tradehero.th.network.service;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.IntermediateCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import timber.log.Timber;

@Singleton public class TranslationServiceWrapper
{
    private final TranslationTokenCache translationTokenCache;
    private final TranslationServiceBingWrapper translationServiceBingWrapper;

    @Inject public TranslationServiceWrapper(
            TranslationTokenCache translationTokenCache,
            TranslationServiceBingWrapper translationServiceBingWrapper)
    {
        this.translationTokenCache = translationTokenCache;
        this.translationServiceBingWrapper = translationServiceBingWrapper;
    }

    public TranslationResult translate(String from, String to, String text)
    {
        TranslationToken token = translationTokenCache.getValid(new TranslationTokenKey());

        if (token instanceof BingTranslationToken)
        {
            return translationServiceBingWrapper.translate((BingTranslationToken) token,
                    from, to, text);
        }
        throw new IllegalArgumentException("Unhandled TranslationToken " + token);
    }

    public MiddleCallback<TranslationResult> translate(String from, String to, String text, Callback<TranslationResult> callback)
    {
        MiddleCallback<TranslationResult> middleCallback = new BaseMiddleCallback<>(callback);
        DTOCache.GetOrFetchTask<TranslationTokenKey, TranslationToken> task = translationTokenCache.getOrFetch(
            new TranslationTokenKey(),
            new TranslationServiceWrapperTokenListener(from, to, text, middleCallback));
        task.execute();
        return middleCallback;
    }

    protected IntermediateCallback<TranslationResult> translate(TranslationToken token, String from, String to, String text, MiddleCallback<TranslationResult> callback)
    {
        if (token instanceof BingTranslationToken)
        {
            return translationServiceBingWrapper.translate((BingTranslationToken) token,
                    from, to, text, callback);
        }
        throw new IllegalArgumentException("Unhandled TranslationToken " + token);
    }

    protected class TranslationServiceWrapperTokenListener implements DTOCache.Listener<TranslationTokenKey, TranslationToken>
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

        @Override public void onDTOReceived(TranslationTokenKey key, TranslationToken value,
                boolean fromCache)
        {
            translate(value, from, to, text, middleCallback);
        }

        @Override public void onErrorThrown(TranslationTokenKey key, Throwable error)
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
}
