package com.tradehero.th.api.trade;

import java.util.Comparator;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;

public class TradeDTODateIncreasingComparator implements Comparator<TradeDTO>
{
    //<editor-fold desc="Constructors">
    @Inject public TradeDTODateIncreasingComparator()
    {
        super();
    }
    //</editor-fold>

    @Override public int compare(@Nullable TradeDTO left, @Nullable TradeDTO right)
    {
        if (left == right)
        {
            return 0;
        }
        if (left == null)
        {
            return -1;
        }
        if (right == null)
        {
            return 1;
        }
        if (left.dateTime == right.dateTime)
        {
            return 0;
        }
        if (left.dateTime == null)
        {
            return -1;
        }
        if (right.dateTime == null)
        {
            return 1;
        }
        return left.dateTime.compareTo(right.dateTime);
    }
}
