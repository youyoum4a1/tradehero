package com.ayondo.academy.fragments.trade;

import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.position.PositionDTOKey;
import com.ayondo.academy.api.trade.TradeDTO;

public class PositionTradeDTOKey implements DTO
{
    public final PositionDTOKey positionDTOKey;
    public final TradeDTO tradeDTO;

    //<editor-fold desc="Constructors">
    public PositionTradeDTOKey(PositionDTOKey positionDTOKey, TradeDTO tradeDTO)
    {
        this.positionDTOKey = positionDTOKey;
        this.tradeDTO = tradeDTO;
    }
    //</editor-fold>
}
