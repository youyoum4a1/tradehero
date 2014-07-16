package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.OwnedTradeIdList;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.network.service.TradeServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class TradeListCache extends StraightCutDTOCacheNew<OwnedPositionId, TradeDTOList, OwnedTradeIdList>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull private final TradeServiceWrapper tradeServiceWrapper;
    @NotNull private final TradeCache tradeCache;

    //<editor-fold desc="Constructors">
    @Inject public TradeListCache(
            @NotNull TradeServiceWrapper tradeServiceWrapper,
            @NotNull TradeCache tradeCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.tradeServiceWrapper = tradeServiceWrapper;
        this.tradeCache = tradeCache;
    }
    //</editor-fold>

    @Override @NotNull public TradeDTOList fetch(@NotNull OwnedPositionId key) throws Throwable
    {
        return tradeServiceWrapper.getTrades(key);
    }

    @NotNull @Override protected OwnedTradeIdList cutValue(
            @NotNull OwnedPositionId key,
            @NotNull TradeDTOList value)
    {
        tradeCache.put(value);
        return new OwnedTradeIdList(value);
    }

    @Nullable @Override protected TradeDTOList inflateValue(
            @NotNull OwnedPositionId key,
            @Nullable OwnedTradeIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        TradeDTOList value = tradeCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
