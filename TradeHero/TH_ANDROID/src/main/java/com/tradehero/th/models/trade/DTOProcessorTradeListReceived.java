package com.tradehero.th.models.trade;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorTradeListReceived implements DTOProcessor<TradeDTOList>
{
    @NotNull private final DTOProcessor<TradeDTO> tradeReceivedProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorTradeListReceived(@NotNull OwnedPositionId ownedPositionId)
    {
        this.tradeReceivedProcessor = new DTOProcessorTradeReceived(ownedPositionId);
    }
    //</editor-fold>

    @Override public TradeDTOList process(TradeDTOList value)
    {
        if (value != null)
        {
            for (TradeDTO tradeDTO : value)
            {
                tradeReceivedProcessor.process(tradeDTO);
            }
        }
        return value;
    }
}
