package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeId;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class TradeIdCache extends StraightDTOCache<TradeId, OwnedTradeId>
{
    public static final String TAG = TradeIdCache.class.getSimpleName();

    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject public TradeIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected OwnedTradeId fetch(TradeId key) throws Throwable
    {
        throw new IllegalStateException("You are not supposed to fetch an individual OwnedTradeId");
    }
}
