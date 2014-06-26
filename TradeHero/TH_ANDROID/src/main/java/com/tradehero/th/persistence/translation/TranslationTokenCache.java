package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.network.service.TranslationTokenServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@Singleton
public class TranslationTokenCache extends StraightDTOCacheNew<TranslationTokenKey, TranslationToken>
{
    @NotNull private final TranslationTokenServiceWrapper translationTokenServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenCache(
            @NotNull TranslationTokenServiceWrapper translationTokenServiceWrapper)
    {
        super(1);
        this.translationTokenServiceWrapper = translationTokenServiceWrapper;
    }
    //</editor-fold>

    @Nullable public TranslationToken getValid(@NotNull TranslationTokenKey key)
    {
        TranslationToken cached = get(key);
        if (cached == null)
        {
            try
            {
                cached = fetch(key);
                put(key, cached);
            }
            catch (Throwable throwable)
            {
                Timber.e(throwable, null);
            }
        }
        return cached;
    }

    @Override public boolean isValid(@NotNull TranslationToken token)
    {
        return token.isValid();
    }

    @Override @NotNull public TranslationToken fetch(@NotNull TranslationTokenKey key) throws Throwable
    {
        return translationTokenServiceWrapper.getToken();
    }
}
