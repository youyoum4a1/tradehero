package com.androidth.general.fragments.trade;

import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.position.PositionDTOKey;
import com.androidth.general.api.trade.TradeDTO;

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
