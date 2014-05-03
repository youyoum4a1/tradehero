package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.OwnedTradeIdList;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.network.service.TradeServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class TradeListCache extends StraightDTOCache<OwnedPositionId, OwnedTradeIdList>
{
    public static final String TAG = TradeListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected TradeServiceWrapper tradeServiceWrapper;
    @Inject protected TradeCache tradeCache;
    @Inject protected TradeIdCache tradeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public TradeListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override protected OwnedTradeIdList fetch(OwnedPositionId key) throws Throwable
    {
        return putInternal(key, tradeServiceWrapper.getTrades(key));
    }

    protected OwnedTradeIdList putInternal(OwnedPositionId key, List<TradeDTO> fleshedValues)
    {
        OwnedTradeIdList tradeIds = null;
        if (fleshedValues != null)
        {
            tradeIds = new OwnedTradeIdList();
            OwnedTradeId ownedTradeId;
            int i = 0;
            for (TradeDTO trade: fleshedValues)
            {
                ownedTradeId = new OwnedTradeId(key, trade.id);
                tradeIds.add(ownedTradeId);
                tradeCache.put(ownedTradeId, trade);
                tradeIdCache.put(trade.getTradeId(), ownedTradeId);
            }
            put(key, tradeIds);
        }
        return tradeIds;
    }
}
