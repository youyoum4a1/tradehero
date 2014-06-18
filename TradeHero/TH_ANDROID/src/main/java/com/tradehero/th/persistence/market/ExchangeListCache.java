package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class ExchangeListCache extends StraightDTOCacheNew<ExchangeListType, ExchangeDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 1; // Be careful to increase when necessary

    @NotNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NotNull private final Lazy<ExchangeIdCache> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeListCache(
            @NotNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NotNull Lazy<ExchangeIdCache> exchangeIdCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override public ExchangeDTOList fetch(@NotNull ExchangeListType key) throws Throwable
    {
        return marketServiceWrapper.get().getExchanges();
    }

    @Override public ExchangeDTOList put(@NotNull ExchangeListType key, @NotNull ExchangeDTOList value)
    {
        exchangeIdCache.get().put(value);
        return super.put(key, value);
    }
}
