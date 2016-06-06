package com.androidth.general.network.retrofit;

import android.content.Context;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.androidth.general.common.annotation.ForApp;
import com.androidth.general.common.log.RetrofitErrorHandlerLogger;
import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.api.ObjectMapperWrapper;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTODeserialiser;
import com.androidth.general.api.position.PositionDTOJacksonModule;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.social.UserFriendsDTODeserialiser;
import com.androidth.general.api.social.UserFriendsDTOJacksonModule;
import com.androidth.general.models.intent.competition.ProviderPageIntent;
import com.androidth.general.network.ApiAuthenticator;
import com.androidth.general.network.CompetitionUrl;
import com.androidth.general.network.NetworkConstants;
import com.androidth.general.network.NullHostNameVerifier;
import com.androidth.general.network.ServerEndpoint;
import com.androidth.general.network.service.RetrofitProtectedModule;
import com.androidth.general.network.service.SocialLinker;
import com.androidth.general.network.service.SocialServiceWrapper;
import com.androidth.general.utils.NetworkUtils;
import com.androidth.general.utils.RetrofitConstants;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.JacksonConverter;

@Module(
        includes = {
                FlavorRetrofitModule.class,
                RetrofitProtectedModule.class,
                RetrofitGameLiveModule.class,
        },
        injects = {
                ProviderPageIntent.class
        },
        complete = false,
        library = true
)
public class RetrofitModule
{
    @Provides JsonDeserializer<PositionDTO> providesPositionDTODeserialiser(PositionDTODeserialiser deserialiser)
    {
        return deserialiser;
    }

    @Provides JsonDeserializer<UserFriendsDTO> providersUserFriendsDTODeserialiser(UserFriendsDTODeserialiser deserialiser)
    {
        return deserialiser;
    }

    @Provides ObjectMapper provideCommonObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Provides @Singleton @ForApp ObjectMapper provideObjectMapper(
            ObjectMapperWrapper objectMapper,
            UserFriendsDTOJacksonModule userFriendsDTOModule,
            PositionDTOJacksonModule positionDTOModule)
    {
        objectMapper.registerModule(userFriendsDTOModule);
        objectMapper.registerModule(positionDTOModule);

        // TODO confirm this is correct here
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.DEFAULT)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        return objectMapper;
    }

    @Provides @Singleton Converter provideConverter(@ForApp ObjectMapper objectMapper)
    {
        return new JacksonConverter(objectMapper);
    }

    @Provides @Singleton Endpoint provideApiServer(@ServerEndpoint StringPreference serverEndpointPreference)
    {
        return Endpoints.newFixedEndpoint(serverEndpointPreference.get());
    }

    @Provides @Singleton @CompetitionUrl String provideCompetitionUrl(Endpoint server)
    {
        return server.getUrl() + NetworkConstants.COMPETITION_PATH;
    }

    @Provides RestAdapter.Builder provideRestAdapterBuilder(
            Client client,
            Converter converter,
            RetrofitSynchronousErrorHandler errorHandler)
    {
        return new RestAdapter.Builder()
                .setConverter(converter)
                .setClient(client)
                .setErrorHandler(errorHandler)
                .setLogLevel(RetrofitConstants.DEFAULT_SERVICE_LOG_LEVEL);
    }

    @Provides @Singleton RestAdapter provideRestAdapter(RestAdapter.Builder builder,
            Endpoint server,
            RequestHeaders requestHeaders,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return builder
                .setEndpoint(server)
                .setRequestInterceptor(requestHeaders)
                .setErrorHandler(errorHandlerLogger)
                .build();
    }

    @Provides @Singleton Client provideOkClient(OkHttpClient okHttpClient)
    {
        return new OkClient(okHttpClient);
    }

    @Provides @Singleton Cache provideHttpCache(Context context)
    {
        File httpCacheDirectory = new File(context.getCacheDir(), "HttpCache");
        return new Cache(httpCacheDirectory, 10 * 1024 * 1024);
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(Cache cache, Authenticator authenticator, HostnameVerifier hostNameVerifier)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(cache);
        okHttpClient.setHostnameVerifier(hostNameVerifier);
        okHttpClient.setSslSocketFactory(NetworkUtils.createBadSslSocketFactory());
        okHttpClient.setAuthenticator(authenticator);
        return okHttpClient;
    }

    @Provides @Singleton Authenticator provideAuthenticator(Lazy<ApiAuthenticator> apiAuthenticator)
    {
        return apiAuthenticator.get();
    }

    @Provides @Singleton HostnameVerifier provideHostnameVerifier(NullHostNameVerifier hostNameVerifier)
    {
        return hostNameVerifier;
    }

    @Provides @Singleton SocialLinker provideSocialLinker(SocialServiceWrapper socialServiceWrapper)
    {
        return socialServiceWrapper;
    }
}
