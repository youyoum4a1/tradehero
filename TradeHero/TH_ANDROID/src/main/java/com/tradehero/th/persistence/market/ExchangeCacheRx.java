package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import rx.Observable;

@Singleton @SystemCache
public class ExchangeCacheRx extends BaseFetchDTOCacheRx<ExchangeIntegerId, ExchangeDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NonNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NonNull private final Lazy<ExchangeIdCacheRx> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCacheRx(
            @NonNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NonNull Lazy<ExchangeIdCacheRx> exchangeIdCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<ExchangeDTO> fetch(@NonNull ExchangeIntegerId key)
    {
        return marketServiceWrapper.get().getExchangeRx(key);
    }

    @Override public void onNext(@NonNull ExchangeIntegerId key, @NonNull ExchangeDTO value)
    {
        exchangeIdCache.get().onNext(value.getExchangeStringId(), key);
        super.onNext(key, value);
    }
}
