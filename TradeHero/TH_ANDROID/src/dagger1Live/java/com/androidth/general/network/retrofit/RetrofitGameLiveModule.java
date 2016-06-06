package com.androidth.general.network.retrofit;

import com.androidth.general.common.log.RetrofitErrorHandlerLogger;
import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.network.ForLive;
import com.androidth.general.network.ServerEndpointLive;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class RetrofitGameLiveModule
{
    @Provides @Singleton @ForLive Endpoint provideLiveApiServer(@ServerEndpointLive StringPreference serverEndpointPreference)
    {
        return Endpoints.newFixedEndpoint(serverEndpointPreference.get());
    }

    @Provides @Singleton @ForLive RestAdapter provideLiveRestAdapter(RestAdapter.Builder builder,
            @ForLive Endpoint server,
            RequestHeaders requestHeaders,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return builder
                .setEndpoint(server)
                .setRequestInterceptor(requestHeaders)
                .setErrorHandler(errorHandlerLogger)
                .build();
    }
}
