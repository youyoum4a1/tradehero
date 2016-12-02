package com.androidth.general.network.retrofit;

import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.network.ForLive;
import com.androidth.general.network.ForLive1B;
import com.androidth.general.network.NetworkConstants;
import com.androidth.general.network.ServerEndpointLive;
import com.androidth.general.network.ServerEndpointLive1B;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit2.Retrofit;

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

    @Provides @Singleton @ForLive
    Retrofit provideLiveRestAdapter(Retrofit.Builder builder
//                                    @ForLive Endpoint server,
//                                    RequestHeaders requestHeaders,
//                                    RetrofitErrorHandlerLogger errorHandlerLogger
    )
    {
        return builder
//                .setEndpoint(server)
                .baseUrl(NetworkConstants.BASE_URL_LIVE)
//                .setRequestInterceptor(requestHeaders)
//                .setErrorHandler(errorHandlerLogger)
                .build();
    }

    @Provides @Singleton @ForLive1B Endpoint provideLive1BApiServer(@ServerEndpointLive1B StringPreference serverEndpointPreference)
    {
        return Endpoints.newFixedEndpoint(serverEndpointPreference.get());
    }

    @Provides @Singleton @ForLive1B Retrofit provideLive1BRestAdapter(Retrofit.Builder builder)
//                                                                     @ForLive1B Endpoint server,
//                                                                     RequestHeaders requestHeaders,
//                                                                     RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return builder
                .baseUrl(NetworkConstants.BASE_URL_LIVE)
//                .setRequestInterceptor(requestHeaders)
//                .setErrorHandler(errorHandlerLogger)
                .build();
    }
}
