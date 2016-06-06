package com.androidth.general.persistence.translation;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.SystemCache;
import com.androidth.general.api.translation.TranslationResult;
import com.androidth.general.network.service.TranslationServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @SystemCache
public class TranslationCacheRx extends BaseFetchDTOCacheRx<TranslationKey, TranslationResult>
{
    private static final int DEFAULT_MAX_VALUE_SIZE = 100;

    @NonNull private final TranslationServiceWrapper translationServiceWrapper;

    @Inject public TranslationCacheRx(
            @NonNull TranslationServiceWrapper translationServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.translationServiceWrapper = translationServiceWrapper;
    }

    @Override @NonNull public Observable<TranslationResult> fetch(@NonNull TranslationKey key)
    {
        return translationServiceWrapper.translateRx(key.from, key.to, key.translatableText);
    }

    @Override public void onNext(@NonNull TranslationKey key, @NonNull TranslationResult value)
    {
        super.onNext(key, value);
        // HACK to limit RAM usage
        key.translatableText = null;
    }
}
