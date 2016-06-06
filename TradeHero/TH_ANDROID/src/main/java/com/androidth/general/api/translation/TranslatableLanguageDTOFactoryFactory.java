package com.androidth.general.api.translation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.androidth.general.api.translation.bing.BingLanguageDTOFactory;
import com.androidth.general.api.translation.bing.BingTranslationToken;
import com.androidth.general.persistence.translation.TranslationTokenCacheRx;
import com.androidth.general.persistence.translation.TranslationTokenKey;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class TranslatableLanguageDTOFactoryFactory
{
    @NonNull private final Lazy<TranslationTokenCacheRx> translationTokenCacheLazy;
    @NonNull private final Provider<BingLanguageDTOFactory> bingLanguageDTOFactoryProvider;

    //<editor-fold desc="Constructors">
    @Inject public TranslatableLanguageDTOFactoryFactory(
            @NonNull Lazy<TranslationTokenCacheRx> translationTokenCacheLazy,
            @NonNull Provider<BingLanguageDTOFactory> bingLanguageDTOFactoryProvider)
    {
        this.translationTokenCacheLazy = translationTokenCacheLazy;
        this.bingLanguageDTOFactoryProvider = bingLanguageDTOFactoryProvider;
    }
    //</editor-fold>

    @NonNull public Observable<TranslatableLanguageDTOFactory> create()
    {
        return translationTokenCacheLazy.get().get(new TranslationTokenKey())
                .map(new Func1<Pair<TranslationTokenKey, TranslationToken>, TranslatableLanguageDTOFactory>()
                {
                    @Override public TranslatableLanguageDTOFactory call(Pair<TranslationTokenKey, TranslationToken> pair)
                    {
                        return TranslatableLanguageDTOFactoryFactory.this.create(pair.second);
                    }
                });
    }

    @Nullable public TranslatableLanguageDTOFactory create(@NonNull TranslationToken type)
    {
        if (type instanceof BingTranslationToken)
        {
            return bingLanguageDTOFactoryProvider.get();
        }
        Timber.e(new IllegalArgumentException(), "Unhandled type %s", type.getClass());
        return null;
    }
}
