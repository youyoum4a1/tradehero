package com.tradehero.th.fragments.trade;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.trade.OwnedTradeId;

public class PositionTradeDTOKey implements DTO
{
    public final PositionDTOKey positionDTOKey;
    public final OwnedTradeId ownedTradeId;

    //<editor-fold desc="Constructors">
    public PositionTradeDTOKey(PositionDTOKey positionDTOKey, OwnedTradeId ownedTradeId)
    {
        this.positionDTOKey = positionDTOKey;
        this.ownedTradeId = ownedTradeId;
    }
    //</editor-fold>
}
