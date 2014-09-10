package com.tradehero;

import com.tradehero.th.activities.MarketUtil;
import dagger.Module;
import dagger.Provides;
import java.lang.IllegalArgumentException;

@Module(
        library = true,
        complete = false
)
public class FlavorModule
{
    @Provides MarketUtil provideMarketUtil()
    {
        throw new IllegalArgumentException("Not Implemented");
    }

    @Provides @Singleton @BaiduPushDeviceIdentifierSentFlag BooleanPreference providePushIdentifierSentFlag(@ForUser SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_PUSH_IDENTIFIER_SENT_FLAG, false);
    }
}
