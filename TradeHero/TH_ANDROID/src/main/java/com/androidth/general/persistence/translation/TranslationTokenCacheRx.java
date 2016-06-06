package com.androidth.general.persistence.translation;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.SystemCache;
import com.androidth.general.api.translation.TranslationToken;
import com.androidth.general.network.service.TranslationTokenServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @SystemCache
public class TranslationTokenCacheRx extends BaseFetchDTOCacheRx<TranslationTokenKey, TranslationToken>
{
    @NonNull private final TranslationTokenServiceWrapper translationTokenServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenCacheRx(
            @NonNull TranslationTokenServiceWrapper translationTokenServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(1, dtoCacheUtil);
        this.translationTokenServiceWrapper = translationTokenServiceWrapper;
    }
    //</editor-fold>

    @Override public boolean isValid(@NonNull TranslationToken token)
    {
        return token.isValid();
    }

    @Override @NonNull protected Observable<TranslationToken> fetch(@NonNull TranslationTokenKey key)
    {
        return translationTokenServiceWrapper.getTokenRx();
    }
}
