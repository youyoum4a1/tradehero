package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.OwnedTradeIdList;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.network.service.TradeServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class TradeListCache extends StraightDTOCacheNew<OwnedPositionId, OwnedTradeIdList>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull private final TradeServiceWrapper tradeServiceWrapper;
    @NotNull private final TradeCache tradeCache;
    @NotNull private final TradeIdCache tradeIdCache;

    //<editor-fold desc="Constructors">
    @Inject public TradeListCache(
            @NotNull TradeServiceWrapper tradeServiceWrapper,
            @NotNull TradeCache tradeCache,
            @NotNull TradeIdCache tradeIdCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.tradeServiceWrapper = tradeServiceWrapper;
        this.tradeCache = tradeCache;
        this.tradeIdCache = tradeIdCache;
    }
    //</editor-fold>

    @Override @NotNull public OwnedTradeIdList fetch(@NotNull OwnedPositionId key) throws Throwable
    {
        return putInternal(key, tradeServiceWrapper.getTrades(key));
    }

    @NotNull protected OwnedTradeIdList putInternal(
            @NotNull OwnedPositionId key,
            @NotNull List<TradeDTO> fleshedValues)
    {
        OwnedTradeIdList tradeIds = new OwnedTradeIdList();
        OwnedTradeId ownedTradeId;
        for (@NotNull TradeDTO trade: fleshedValues)
        {
            ownedTradeId = new OwnedTradeId(key, trade.id);
            tradeIds.add(ownedTradeId);
            tradeCache.put(ownedTradeId, trade);
            tradeIdCache.put(trade.getTradeId(), ownedTradeId);
        }
        put(key, tradeIds);
        return tradeIds;
    }
}
