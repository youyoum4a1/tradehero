package com.tradehero.th.api.translation;

import com.tradehero.th.api.translation.bing.BingLanguageDTOFactory;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.persistence.translation.TranslationTokenCacheRx;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import timber.log.Timber;

public class TranslatableLanguageDTOFactoryFactory
{
    @NotNull private final Lazy<TranslationTokenCacheRx> translationTokenCacheLazy;
    @NotNull private final Provider<BingLanguageDTOFactory> bingLanguageDTOFactoryProvider;

    //<editor-fold desc="Constructors">
    @Inject public TranslatableLanguageDTOFactoryFactory(
            @NotNull Lazy<TranslationTokenCacheRx> translationTokenCacheLazy,
            @NotNull Provider<BingLanguageDTOFactory> bingLanguageDTOFactoryProvider)
    {
        this.translationTokenCacheLazy = translationTokenCacheLazy;
        this.bingLanguageDTOFactoryProvider = bingLanguageDTOFactoryProvider;
    }
    //</editor-fold>

    @NotNull public Observable<TranslatableLanguageDTOFactory> create()
    {
        return translationTokenCacheLazy.get().get(new TranslationTokenKey())
                .map(pair -> create(pair.second));
    }

    @Nullable public TranslatableLanguageDTOFactory create(@NotNull TranslationToken type)
    {
        if (type instanceof BingTranslationToken)
        {
            return bingLanguageDTOFactoryProvider.get();
        }
        Timber.e(new IllegalArgumentException(), "Unhandled type %s", type.getClass());
        return null;
    }
}
