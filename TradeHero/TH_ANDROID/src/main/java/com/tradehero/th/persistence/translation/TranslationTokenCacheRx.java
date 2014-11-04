package com.tradehero.th.persistence.translation;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.th.api.translation.TranslationToken;
import com.tradehero.th.network.service.TranslationTokenServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @SystemCache
public class TranslationTokenCacheRx extends BaseFetchDTOCacheRx<TranslationTokenKey, TranslationToken>
{
    @NotNull private final TranslationTokenServiceWrapper translationTokenServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public TranslationTokenCacheRx(
            @NotNull TranslationTokenServiceWrapper translationTokenServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(1, 1, 1, dtoCacheUtil);
        this.translationTokenServiceWrapper = translationTokenServiceWrapper;
    }
    //</editor-fold>

    @Override public boolean isValid(@NotNull TranslationToken token)
    {
        return token.isValid();
    }

    @Override @NotNull protected Observable<TranslationToken> fetch(@NotNull TranslationTokenKey key)
    {
        return translationTokenServiceWrapper.getTokenRx();
    }
}
