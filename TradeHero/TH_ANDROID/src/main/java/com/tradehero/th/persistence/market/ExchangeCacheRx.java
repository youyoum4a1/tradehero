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
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @SystemCache
public class ExchangeCacheRx extends BaseFetchDTOCacheRx<ExchangeIntegerId, ExchangeDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NotNull private final Lazy<ExchangeIdCacheRx> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCacheRx(
            @NotNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NotNull Lazy<ExchangeIdCacheRx> exchangeIdCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<ExchangeDTO> fetch(@NotNull ExchangeIntegerId key)
    {
        return marketServiceWrapper.get().getExchangeRx(key);
    }

    @Override public void onNext(@NotNull ExchangeIntegerId key, @NotNull ExchangeDTO value)
    {
        exchangeIdCache.get().onNext(value.getExchangeStringId(), key);
        super.onNext(key, value);
    }
}
