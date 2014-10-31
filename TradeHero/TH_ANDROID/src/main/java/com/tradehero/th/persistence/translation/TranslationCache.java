package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.network.service.TranslationServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton @SystemCache
public class TranslationCache extends StraightDTOCacheNew<TranslationKey, TranslationResult>
{
    @NotNull private final TranslationServiceWrapper translationServiceWrapper;

    @Inject public TranslationCache(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull TranslationServiceWrapper translationServiceWrapper,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize.get(), dtoCacheUtil);
        this.translationServiceWrapper = translationServiceWrapper;
    }

    @Override @NotNull public TranslationResult fetch(@NotNull TranslationKey key) throws Throwable
    {
        return translationServiceWrapper.translate(key.from, key.to, key.translatableText);
    }

    @Override @Nullable public TranslationResult put(@NotNull TranslationKey key, @NotNull TranslationResult value)
    {
        TranslationResult previous = super.put(key, value);
        // HACK to limit RAM usage
        key.translatableText = null;
        return previous;
    }
}
