package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class ExchangeCompactListCacheRx extends BaseFetchDTOCacheRx<ExchangeListType, ExchangeCompactDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1; // Be careful to increase when necessary

    @NotNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NotNull private final Lazy<ExchangeIdCacheRx> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCompactListCacheRx(
            @NotNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NotNull Lazy<ExchangeIdCacheRx> exchangeIdCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<ExchangeCompactDTOList> fetch(@NotNull ExchangeListType key)
    {
        return marketServiceWrapper.get().getExchangesRx();
    }

    @Override public void onNext(@NotNull ExchangeListType key, @NotNull ExchangeCompactDTOList value)
    {
        exchangeIdCache.get().onNext(value);
        super.onNext(key, value);
    }
}
