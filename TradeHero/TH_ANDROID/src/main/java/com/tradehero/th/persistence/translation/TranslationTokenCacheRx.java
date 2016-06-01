package com.ayondo.academy.persistence.translation;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.ayondo.academy.api.translation.TranslationToken;
import com.ayondo.academy.network.service.TranslationTokenServiceWrapper;
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
