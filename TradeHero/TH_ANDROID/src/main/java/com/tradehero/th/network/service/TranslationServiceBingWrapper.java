package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.translation.bing.BingTranslationResult;
import com.ayondo.academy.api.translation.bing.BingTranslationToken;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

@Singleton public class TranslationServiceBingWrapper
{
    private static final String PREFERRED_CONTENT_TYPE = "text/plain";

    @NonNull private final TranslationServiceBingRx translationServiceBingRx;

    //<editor-fold desc="Constructors">
    @Inject public TranslationServiceBingWrapper(
            @NonNull TranslationServiceBingRx translationServiceBingRx)
    {
        this.translationServiceBingRx = translationServiceBingRx;
    }
    //</editor-fold>

    @NonNull public Observable<BingTranslationResult> translateRx(@NonNull BingTranslationToken token, final String from, final String to, String text)
    {
        return translationServiceBingRx.requestForTranslation(
                token.getPrefixedAccessToken(),
                from, to, PREFERRED_CONTENT_TYPE, text)
                .map(new Func1<String, BingTranslationResult>()
                {
                    @Override public BingTranslationResult call(String translated)
                    {
                        if (translated.startsWith("TranslateApiException"))
                        {
                            Timber.e(new RuntimeException(), translated);
                            throw new RuntimeException(translated);
                        }
                        return new BingTranslationResult(from, to, translated);
                    }
                });
    }
}
