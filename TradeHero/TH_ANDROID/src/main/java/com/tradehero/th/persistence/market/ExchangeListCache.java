package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.network.service.MarketService;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ExchangeListCache extends StraightDTOCacheNew<ExchangeListType, ExchangeDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 1; // Be careful to increase when necessary

    @Inject Lazy<MarketService> marketService;
    @Inject Lazy<ExchangeIdCache> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public ExchangeDTOList fetch(ExchangeListType key) throws Throwable
    {
        return new ExchangeDTOList(marketService.get().getExchanges());
    }

    @Override public ExchangeDTOList put(ExchangeListType key, ExchangeDTOList value)
    {
        exchangeIdCache.get().put(value);
        return super.put(key, value);
    }
}
