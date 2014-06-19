package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.network.service.MarketService;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class ExchangeListCache extends StraightDTOCacheNew<ExchangeListType, ExchangeDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 1; // Be careful to increase when necessary

    @NotNull private final Lazy<MarketService> marketService;
    @NotNull private final Lazy<ExchangeIdCache> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeListCache(
            @NotNull Lazy<MarketService> marketService,
            @NotNull Lazy<ExchangeIdCache> exchangeIdCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.marketService = marketService;
        this.exchangeIdCache = exchangeIdCache;
    }
    //</editor-fold>

    @Override public ExchangeDTOList fetch(@NotNull ExchangeListType key) throws Throwable
    {
        return new ExchangeDTOList(marketService.get().getExchanges());
    }

    @Override public ExchangeDTOList put(@NotNull ExchangeListType key, @NotNull ExchangeDTOList value)
    {
        exchangeIdCache.get().put(value);
        return super.put(key, value);
    }
}
