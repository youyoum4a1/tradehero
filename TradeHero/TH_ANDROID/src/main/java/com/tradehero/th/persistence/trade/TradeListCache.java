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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class TradeListCache extends StraightDTOCache<OwnedPositionId, OwnedTradeIdList>
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

    @Override protected OwnedTradeIdList fetch(@NotNull OwnedPositionId key) throws Throwable
    {
        return putInternal(key, tradeServiceWrapper.getTrades(key));
    }

    @Contract("_, null -> null; _, !null -> !null") @Nullable
    protected OwnedTradeIdList putInternal(
            @NotNull OwnedPositionId key,
            @Nullable List<TradeDTO> fleshedValues)
    {
        OwnedTradeIdList tradeIds = null;
        if (fleshedValues != null)
        {
            tradeIds = new OwnedTradeIdList();
            OwnedTradeId ownedTradeId;
            int i = 0;
            for (@NotNull TradeDTO trade: fleshedValues)
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
