package com.tradehero;

import com.tradehero.th.activities.SamsungMarketUtil;
import com.tradehero.th.activities.MarketUtil;
import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public class FlavorModule
{
    @Provides MarketUtil provideMarketUtil(SamsungMarketUtil marketUtil)
    {
        return marketUtil;
    }
}
