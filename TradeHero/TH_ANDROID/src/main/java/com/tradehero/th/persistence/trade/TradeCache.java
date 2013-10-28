package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeId;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by julien on 22/10/13
 */
@Singleton public class TradeCache extends StraightDTOCache <TradeId, TradeDTO>
{
    public static final String TAG = TradeCache.class.getSimpleName();
    private static final int DEFAULT_MAX_SIZE = 500;

    @Inject public TradeCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected TradeDTO fetch(TradeId key) throws Throwable
    {
        throw new IllegalStateException("You are not supposed to fetch an individual TradeDTO");
    }
}
