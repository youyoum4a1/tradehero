package com.tradehero.th.models.trade;

import android.support.annotation.NonNull;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.ThroughDTOProcessor;

public class DTOProcessorTradeListReceived extends ThroughDTOProcessor<TradeDTOList>
{
    @NonNull private final DTOProcessor<TradeDTO> tradeReceivedProcessor;

    //<editor-fold desc="Constructors">
    public DTOProcessorTradeListReceived(@NonNull OwnedPositionId ownedPositionId)
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
