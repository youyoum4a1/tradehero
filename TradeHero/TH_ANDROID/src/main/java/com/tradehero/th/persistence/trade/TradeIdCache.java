package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeId;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class TradeIdCache extends StraightDTOCacheNew<TradeId, OwnedTradeId>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject public TradeIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override public OwnedTradeId fetch(@NotNull TradeId key) throws Throwable
    {
        throw new IllegalStateException("You are not supposed to fetch an individual OwnedTradeId");
    }
}
