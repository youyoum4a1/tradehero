package com.tradehero.th.persistence.trade;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class TradeCache extends StraightDTOCacheNew<OwnedTradeId, TradeDTO>
{
    private static final int DEFAULT_MAX_SIZE = 500;

    @Inject public TradeCache()
    {
        super(DEFAULT_MAX_SIZE);
    }

    @Override @NotNull public TradeDTO fetch(@NotNull OwnedTradeId key) throws Throwable
    {
        throw new IllegalStateException("You are not supposed to fetch an individual TradeDTO");
    }

    @NotNull public TradeDTOList put(@NotNull List<TradeDTO> tradeDTOs)
    {
        TradeDTOList previous = new TradeDTOList();
        for (@NotNull TradeDTO tradeDTO : tradeDTOs)
        {
            previous.add(put(tradeDTO.getOwnedTradeId(), tradeDTO));
        }
        return previous;
    }

    @NotNull public TradeDTOList get(@NotNull List<OwnedTradeId> tradeIds)
    {
        TradeDTOList cached = new TradeDTOList();
        for (@NotNull OwnedTradeId ownedTradeId : tradeIds)
        {
            cached.add(get(ownedTradeId));
        }
        return cached;
    }
}
