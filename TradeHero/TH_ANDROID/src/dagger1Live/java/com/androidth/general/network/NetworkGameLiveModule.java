package com.androidth.general.network;

import android.content.SharedPreferences;
import com.androidth.general.common.annotation.ForApp;
import com.androidth.general.common.persistence.prefs.StringPreference;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
        },
        complete = false,
        library = true
)
public class NetworkGameLiveModule
{
    public static final String SERVER_LIVE_ENDPOINT_KEY = "SERVER_LIVE_ENDPOINT_KEY";

    @Provides @Singleton @ServerEndpointLive
    StringPreference provideEndpointPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, SERVER_LIVE_ENDPOINT_KEY, LiveNetworkConstants.TRADEHERO_LIVE_API_ENDPOINT);
    }
}
