package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.network.service.MarketServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class ExchangeCache extends StraightDTOCacheNew<ExchangeIntegerId, ExchangeDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final Lazy<MarketServiceWrapper> marketServiceWrapper;
    @NotNull private final Lazy<ExchangeIdCache> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCache(
            @NotNull Lazy<MarketServiceWrapper> marketServiceWrapper,
            @NotNull Lazy<ExchangeIdCache> exchangeIdCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.marketServiceWrapper = marketServiceWrapper;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override public ExchangeDTO fetch(@NotNull ExchangeIntegerId key) throws Throwable
    {
        return marketServiceWrapper.get().getExchange(key);
    }

    @Override public ExchangeDTO put(@NotNull ExchangeIntegerId key, @NotNull ExchangeDTO value)
    {
        exchangeIdCache.get().put(value.getExchangeStringId(), key);
        return super.put(key, value);
    }
}
