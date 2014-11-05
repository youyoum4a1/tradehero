package com.tradehero.th.api.trade;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;
import android.support.annotation.NonNull;

@Deprecated
public class OwnedTradeIdList extends DTOKeyIdList<OwnedTradeId>
{
    //<editor-fold desc="Constructors">
    public OwnedTradeIdList()
    {
        super();
    }

    public OwnedTradeIdList(Collection<? extends TradeDTO> tradeDTOs)
    {
        for (TradeDTO tradeDTO : tradeDTOs)
        {
            add(tradeDTO.getOwnedTradeId());
        }
    }
    //</editor-fold>
}
