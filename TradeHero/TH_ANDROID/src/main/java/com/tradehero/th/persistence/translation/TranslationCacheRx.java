package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.network.service.TranslationServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @SystemCache
public class TranslationCacheRx extends BaseFetchDTOCacheRx<TranslationKey, TranslationResult>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 100;
    private static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final TranslationServiceWrapper translationServiceWrapper;

    @Inject public TranslationCacheRx(
            @NotNull TranslationServiceWrapper translationServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.translationServiceWrapper = translationServiceWrapper;
    }

    @Override @NotNull public Observable<TranslationResult> fetch(@NotNull TranslationKey key)
    {
        return translationServiceWrapper.translateRx(key.from, key.to, key.translatableText);
    }

    @Override public void onNext(@NotNull TranslationKey key, @NotNull TranslationResult value)
    {
        super.onNext(key, value);
        // HACK to limit RAM usage
        key.translatableText = null;
    }
}
