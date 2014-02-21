package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.network.service.MarketService;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 6:28 PM To change this template use File | Settings | File Templates. */
@Singleton public class ExchangeCache extends StraightDTOCache<ExchangeIntegerId, ExchangeDTO>
{
    public static final String TAG = ExchangeCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject Lazy<MarketService> marketService;
    @Inject Lazy<ExchangeIdCache> exchangeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ExchangeDTO fetch(ExchangeIntegerId key) throws Throwable
    {
        return marketService.get().getExchange(key.key);
    }

    @Override public ExchangeDTO put(ExchangeIntegerId key, ExchangeDTO value)
    {
        exchangeIdCache.get().put(value.getExchangeStringId(), key);
        return super.put(key, value);
    }
}
