package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.network.service.MarketService;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ExchangeCache extends StraightDTOCacheNew<ExchangeIntegerId, ExchangeDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject Lazy<MarketService> marketService;
    @Inject Lazy<ExchangeIdCache> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override public ExchangeDTO fetch(ExchangeIntegerId key) throws Throwable
    {
        return marketService.get().getExchange(key.key);
    }

    @Override public ExchangeDTO put(ExchangeIntegerId key, ExchangeDTO value)
    {
        exchangeIdCache.get().put(value.getExchangeStringId(), key);
        return super.put(key, value);
    }
}
