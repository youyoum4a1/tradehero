package com.androidth.general.models.trade;

import android.support.annotation.NonNull;
import com.androidth.general.api.position.OwnedPositionId;
import com.androidth.general.api.trade.TradeDTO;
import com.androidth.general.models.ThroughDTOProcessor;

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
