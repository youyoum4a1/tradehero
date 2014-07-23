package com.tradehero.th.api.trade;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class OwnedTradeIdList extends DTOKeyIdList<OwnedTradeId>
{
    //<editor-fold desc="Constructors">
    public OwnedTradeIdList()
    {
        super();
    }

    public OwnedTradeIdList(Collection<? extends TradeDTO> tradeDTOs)
    {
        for (@NotNull TradeDTO tradeDTO : tradeDTOs)
        {
            add(tradeDTO.getOwnedTradeId());
        }
    }
    //</editor-fold>
}
