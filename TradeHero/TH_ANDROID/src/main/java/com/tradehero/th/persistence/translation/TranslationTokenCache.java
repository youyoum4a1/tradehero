package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.network.service.TranslationTokenServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Singleton
public class TranslationTokenCache extends StraightDTOCacheNew<TranslationTokenKey, TranslationToken>
{
    private final TranslationTokenServiceWrapper translationTokenServiceWrapper;

    @Inject public TranslationTokenCache(TranslationTokenServiceWrapper translationTokenServiceWrapper)
    {
        super(1);
        this.translationTokenServiceWrapper = translationTokenServiceWrapper;
    }

    public TranslationToken getValid(TranslationTokenKey key)
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
                Timber.e(throwable, "");
            }
        }
        return cached;
    }

    @Override public TranslationToken get(@NotNull TranslationTokenKey key)
    {
        TranslationToken token = super.get(key);
        if (token == null || !token.isValid())
        {
            // This will do the trick to force redownload.
            return null;
        }
        return token;
    }

    @Override public TranslationToken fetch(@NotNull TranslationTokenKey key) throws Throwable
    {
        return translationTokenServiceWrapper.getToken();
    }
}
