package com.tradehero.th.models.trade;

import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.models.DTOProcessor;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorTradeReceived implements DTOProcessor<TradeDTO>
{
    @NotNull private final OwnedPositionId ownedPositionId;

    //<editor-fold desc="Constructors">
    public DTOProcessorTradeReceived(@NotNull OwnedPositionId ownedPositionId)
    {
        this.ownedPositionId = new OwnedPositionId(
                ownedPositionId.userId,
                ownedPositionId.portfolioId,
                ownedPositionId.positionId);
    }
    //</editor-fold>

    @Override public TradeDTO process(TradeDTO value)
    {
        if (value != null)
        {
            value.userId = ownedPositionId.userId;
            value.portfolioId = ownedPositionId.portfolioId;
            value.positionId = ownedPositionId.positionId;
        }
        return value;
    }
}
