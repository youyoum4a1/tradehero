package com.tradehero.th.api.trade;

import android.support.annotation.Nullable;
import java.io.Serializable;
import java.util.Comparator;
import javax.inject.Inject;

public class TradeDTODateIncreasingComparator implements Comparator<TradeDTO>, Serializable
{
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
