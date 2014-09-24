package com.tradehero;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.activities.MarketUtil;
import com.tradehero.th.persistence.prefs.BaiduPushDeviceIdentifierSentFlag;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        library = true,
        complete = false
)
public class FlavorModule
{
    private static final String PREF_PUSH_IDENTIFIER_SENT_FLAG = "PREF_PUSH_IDENTIFIER_SENT_FLAG";
    @Provides MarketUtil provideMarketUtil()
    {
        throw new IllegalArgumentException("Not Implemented");
    }

    @Provides @Singleton @BaiduPushDeviceIdentifierSentFlag BooleanPreference providePushIdentifierSentFlag(@ForUser
    SharedPreferences sharedPreferences)
    {
        return new BooleanPreference(sharedPreferences, PREF_PUSH_IDENTIFIER_SENT_FLAG, false);
    }
}
