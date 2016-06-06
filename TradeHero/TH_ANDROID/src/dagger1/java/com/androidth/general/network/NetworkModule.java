package com.androidth.general.network;

import android.content.SharedPreferences;
import com.androidth.general.common.annotation.ForApp;
import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.network.retrofit.RetrofitModule;
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
