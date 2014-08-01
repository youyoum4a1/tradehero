package com.tradehero.th.fragments.trade;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.trade.TradeDTO;

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
