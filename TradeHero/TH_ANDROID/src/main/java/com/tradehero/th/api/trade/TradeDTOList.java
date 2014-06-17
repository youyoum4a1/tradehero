package com.tradehero.th.api.trade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import org.jetbrains.annotations.Nullable;

public class TradeDTOList extends ArrayList<TradeDTO>
{
    //<editor-fold desc="Constructors">
    public TradeDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public TradeDTOList()
    {
        super();
    }

    public TradeDTOList(Collection<? extends TradeDTO> c)
    {
        super(c);
    }
    //</editor-fold>

    @Nullable
    public TradeDTO getLatestTrade()
    {
        TradeDTO latest = null;
        Comparator<TradeDTO> dateIncreasingComparator = new TradeDTODateIncreasingComparator();
        for (TradeDTO tradeDTO : this)
        {
            if (dateIncreasingComparator.compare(latest, tradeDTO) >= 0)
            {
                latest = tradeDTO;
            }
        }
        return latest;
    }
}
