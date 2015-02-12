package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.translation.bing.BingTranslationResult;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

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

    @NonNull public Observable<BingTranslationResult> translateRx(@NonNull BingTranslationToken token, String from, String to, String text)
    {
        return translationServiceBingRx.requestForTranslation(
                token.getPrefixedAccessToken(),
                from, to, PREFERRED_CONTENT_TYPE, text)
                .map(new Func1<String, BingTranslationResult>()
                {
                    @Override public BingTranslationResult call(String translated)
                    {
                        return new BingTranslationResult(from, to, translated);
                    }
                });
    }
}
