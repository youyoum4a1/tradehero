package com.ayondo.academy.models.trade;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.position.OwnedPositionId;
import com.ayondo.academy.api.trade.TradeDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;

public class DTOProcessorTradeReceived extends ThroughDTOProcessor<TradeDTO>
{
    @NonNull private final OwnedPositionId ownedPositionId;

    //<editor-fold desc="Constructors">
    public DTOProcessorTradeReceived(@NonNull OwnedPositionId ownedPositionId)
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
