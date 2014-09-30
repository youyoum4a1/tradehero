package com.tradehero.th.network;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.network.retrofit.RetrofitModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
                RetrofitModule.class
        },
        complete = false,
        library = true
)
public class NetworkModule
{
    public static final String SERVER_ENDPOINT_KEY = "SERVER_ENDPOINT_KEY";

    @Provides @Singleton @ServerEndpoint
    StringPreference provideEndpointPreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, SERVER_ENDPOINT_KEY, NetworkConstants.getApiEndPointInUse());
    }
}
