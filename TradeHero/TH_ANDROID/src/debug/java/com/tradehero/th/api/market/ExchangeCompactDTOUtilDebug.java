package com.tradehero.th.api.market;

import android.support.annotation.NonNull;
import java.util.Arrays;

public class ExchangeCompactDTOUtilDebug
{
    @Deprecated // Server should do it
    public static void tempPopulate(@NonNull ExchangeCompactDTO exchange)
    {
        for (MarketRegion region : MarketRegion.values())
        {
            if (Arrays.asList(region.exchanges).contains(exchange.name))
            {
                exchange.region = region;
                return;
            }
        }
    }
}
