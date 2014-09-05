package com.tradehero;

import com.tradehero.th.activities.GooglePlayMarketUtil;
import com.tradehero.th.activities.MarketUtil;
import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public class FlavorModule
{
    @Provides MarketUtil provideMarketUtil(GooglePlayMarketUtil marketUtil)
    {
        return marketUtil;
    }
}
