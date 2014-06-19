package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.network.service.TranslationServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class TranslationCache extends StraightDTOCacheNew<TranslationKey, TranslationResult>
{
    private final TranslationServiceWrapper translationServiceWrapper;

    @Inject public TranslationCache(
            @SingleCacheMaxSize IntPreference maxSize,
            TranslationServiceWrapper translationServiceWrapper)
    {
        super(maxSize.get());
        this.translationServiceWrapper = translationServiceWrapper;
    }

    @Override public TranslationResult fetch(@NotNull TranslationKey key) throws Throwable
    {
        return translationServiceWrapper.translate(key.from, key.to, key.translatableText);
    }

    @Override public TranslationResult put(@NotNull TranslationKey key, @NotNull TranslationResult value)
    {
        TranslationResult previous = super.put(key, value);
        // HACK to limit RAM usage
        key.translatableText = null;
        return previous;
    }
}
