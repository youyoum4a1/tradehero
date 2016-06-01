package com.ayondo.academy.network;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.ayondo.academy.network.retrofit.RetrofitModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
                RetrofitModule.class,
                NetworkGameLiveModule.class,
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
