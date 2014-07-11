package com.tradehero.th.api.translation;

import com.tradehero.th.api.translation.bing.BingLanguageDTOFactory;
import com.tradehero.th.api.translation.bing.BingTranslationToken;
import com.tradehero.th.persistence.translation.TranslationTokenCache;
import com.tradehero.th.persistence.translation.TranslationTokenKey;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class TranslatableLanguageDTOFactoryFactory
{
    @NotNull private final Lazy<TranslationTokenCache> translationTokenCacheLazy;
    @NotNull private final Provider<BingLanguageDTOFactory> bingLanguageDTOFactoryProvider;

    //<editor-fold desc="Constructors">
    @Inject public TranslatableLanguageDTOFactoryFactory(
            @NotNull Lazy<TranslationTokenCache> translationTokenCacheLazy,
            @NotNull Provider<BingLanguageDTOFactory> bingLanguageDTOFactoryProvider)
    {
        this.translationTokenCacheLazy = translationTokenCacheLazy;
        this.bingLanguageDTOFactoryProvider = bingLanguageDTOFactoryProvider;
    }
    //</editor-fold>

    @Nullable TranslatableLanguageDTOFactory create()
    {
        TranslationToken token = translationTokenCacheLazy.get().get(new TranslationTokenKey());
        return token != null ? create(token) : null;
    }

    @Nullable TranslatableLanguageDTOFactory create(@NotNull TranslationToken type)
    {
        if (type instanceof BingTranslationToken)
        {
            return bingLanguageDTOFactoryProvider.get();
        }
        Timber.e(new IllegalArgumentException(), "Unhandled type %s", type.getClass());
        return null;
    }
}
