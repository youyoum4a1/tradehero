package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.translation.TranslationToken;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class TranslationTokenServiceWrapper
{
    @NonNull private final TranslationTokenServiceRx translationTokenServiceRx;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenServiceWrapper(
            @NonNull TranslationTokenServiceRx translationTokenServiceRx)
    {
        this.translationTokenServiceRx = translationTokenServiceRx;
    }
    //</editor-fold>

    //<editor-fold desc="Get Token">
    @NonNull public Observable<TranslationToken> getTokenRx()
    {
        return translationTokenServiceRx.requestToken();
    }
    //</editor-fold>
}
