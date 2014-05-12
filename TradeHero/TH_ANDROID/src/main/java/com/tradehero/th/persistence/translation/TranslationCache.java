package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.network.service.TranslationServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class TranslationCache extends StraightDTOCache<TranslationKey, TranslationResult>
{
    private final TranslationServiceWrapper translationServiceWrapper;

    @Inject public TranslationCache(
            @SingleCacheMaxSize IntPreference maxSize,
            TranslationServiceWrapper translationServiceWrapper)
    {
        super(maxSize.get());
        this.translationServiceWrapper = translationServiceWrapper;
    }

    @Override protected TranslationResult fetch(TranslationKey key) throws Throwable
    {
        return translationServiceWrapper.translate(key.from, key.to, key.translatableText);
    }

    @Override public TranslationResult put(TranslationKey key, TranslationResult value)
    {
        TranslationResult previous = super.put(key, value);
        // HACK to limit RAM usage
        key.translatableText = null;
        return previous;
    }
}
