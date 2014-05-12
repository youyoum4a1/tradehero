package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class TradeCache extends StraightDTOCache <OwnedTradeId, TradeDTO>
{
    private static final int DEFAULT_MAX_SIZE = 500;

    @Inject public TradeCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected TradeDTO fetch(OwnedTradeId key) throws Throwable
    {
        throw new IllegalStateException("You are not supposed to fetch an individual TradeDTO");
    }
}
