package com.tradehero.th.activities;

import android.content.Intent;
import android.content.IntentFilter;
import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class ActivityAppModule
{
    public static final String UPGRADE_INTENT_ACTION_NAME = "com.tradehero.th.upgrade.ALERT";
    public static final String RENEW_TOKEN_INTENT_ACTION_NAME = "com.tradehero.th.auth.token.ALERT";

    @Provides @ForUpgrade IntentFilter provideIntentFilterUpgrade()
    {
        return new IntentFilter(UPGRADE_INTENT_ACTION_NAME);
    }

    @Provides @ForUpgrade Intent provideIntentUpgrade()
    {
        return new Intent(UPGRADE_INTENT_ACTION_NAME);
    }

    @Provides @ForSocialToken IntentFilter provideIntentFilterSocialToken()
    {
        return new IntentFilter(RENEW_TOKEN_INTENT_ACTION_NAME);
    }

    @Provides @ForSocialToken Intent provideIntentSocialToken()
    {
        return new Intent(RENEW_TOKEN_INTENT_ACTION_NAME);
    }
}
