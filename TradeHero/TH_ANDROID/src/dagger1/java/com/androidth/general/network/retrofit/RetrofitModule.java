package com.androidth.general.network.retrofit;

import android.content.Context;

import com.androidth.general.api.ObjectMapperWrapper;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTODeserialiser;
import com.androidth.general.api.position.PositionDTOJacksonModule;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.social.UserFriendsDTODeserialiser;
import com.androidth.general.api.social.UserFriendsDTOJacksonModule;
import com.androidth.general.common.annotation.ForApp;
import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.models.intent.competition.ProviderPageIntent;
import com.androidth.general.network.ApiAuthenticator;
import com.androidth.general.network.CompetitionUrl;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.NetworkConstants;
import com.androidth.general.network.NullHostNameVerifier;
import com.androidth.general.network.ServerEndpoint;
import com.androidth.general.network.service.RetrofitProtectedModule;
import com.androidth.general.network.service.SocialLinker;
import com.androidth.general.network.service.SocialServiceWrapper;
import com.androidth.general.utils.NetworkUtils;
import com.androidth.general.utils.RetrofitConstants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.RxThreadFactory;
import rx.schedulers.Schedulers;

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

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // TODO confirm this is correct here
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.DEFAULT)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        return objectMapper;
    }

    @Provides @Singleton
    JacksonConverterFactory provideConverter(@ForApp ObjectMapper objectMapper)
    {
        return JacksonConverterFactory.create(objectMapper);
    }

//    @Provides @Singleton Endpoint provideApiServer(@ServerEndpoint StringPreference serverEndpointPreference)
//    {
//        return Endpoints.newFixedEndpoint(serverEndpointPreference.get());
//    }
//
//    @Provides @Singleton @CompetitionUrl String provideCompetitionUrl(Endpoint server)
//    {
//        return LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT + NetworkConstants.COMPETITION_PATH;
//    }

    @Provides @Singleton String provideApiServer(@ServerEndpoint StringPreference serverEndpointPreference)
    {
        return serverEndpointPreference.get();
    }

    @Provides @Singleton @CompetitionUrl String provideCompetitionUrl()
    {
        return LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT + NetworkConstants.COMPETITION_PATH;
    }

    @Provides
    Retrofit.Builder provideRestAdapterBuilder(
            OkHttpClient client,
            ObjectMapper objectMapper)
//            Converter converter
//            RetrofitSynchronousErrorHandler errorHandler)
    {
        return new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(new RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
                .client(client)
//                .setErrorHandler(errorHandler)
//                .setLogLevel(RetrofitConstants.DEFAULT_SERVICE_LOG_LEVEL)
                ;
    }

    @Provides @Singleton Retrofit provideRestAdapter(Retrofit.Builder builder,
//            Endpoint server,
            RequestHeaders requestHeaders
//            RetrofitErrorHandlerLogger errorHandlerLogger
    )
    {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(requestHeaders)
                .build();

        return builder
                .baseUrl("https://www.tradehero.mobi/")
                .client(client)
//                .setRequestInterceptor(requestHeaders)
//                .setErrorHandler(errorHandlerLogger)
                .addCallAdapterFactory(new RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
                .build();
    }

    @Provides @Singleton OkHttpClient.Builder provideOkClient(OkHttpClient okHttpClient)
    {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        return client.newBuilder();
    }

    @Provides @Singleton
    Cache provideHttpCache(Context context)
    {
        File httpCacheDirectory = new File(context.getCacheDir(), "HttpCache");
        return new Cache(httpCacheDirectory, 10 * 1024 * 1024);
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(Cache cache, Authenticator authenticator, HostnameVerifier hostNameVerifier)
    {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.setCache(cache);
//        okHttpClient.setHostnameVerifier(hostNameVerifier);
//        okHttpClient.setSslSocketFactory(NetworkUtils.createBadSslSocketFactory());
//        okHttpClient.setAuthenticator(authenticator);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .cache(cache)
                .hostnameVerifier(hostNameVerifier)
                .sslSocketFactory(NetworkUtils.createBadSslSocketFactory(), NetworkUtils.getTrustManager())
                .authenticator(authenticator)
                .build();

        return client;
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

 class RxThreadCallAdapter extends CallAdapter.Factory {

    RxJavaCallAdapterFactory rxFactory = RxJavaCallAdapterFactory.create();
    private Scheduler subscribeScheduler;
    private Scheduler observerScheduler;

    public RxThreadCallAdapter(Scheduler subscribeScheduler, Scheduler observerScheduler) {
        this.subscribeScheduler = subscribeScheduler;
        this.observerScheduler = observerScheduler;
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter<Observable<?>> callAdapter = (CallAdapter<Observable<?>>) rxFactory.get(returnType, annotations, retrofit);
        return callAdapter != null ? new ThreadCallAdapter(callAdapter) : null;
    }

    final class ThreadCallAdapter implements CallAdapter<Observable<?>> {
        CallAdapter<Observable<?>> delegateAdapter;

        ThreadCallAdapter(CallAdapter<Observable<?>> delegateAdapter) {
            this.delegateAdapter = delegateAdapter;
        }

        @Override public Type responseType() {
            return delegateAdapter.responseType();
        }

        @Override
        public <T> Observable<?> adapt(Call<T> call) {
            return delegateAdapter.adapt(call).subscribeOn(subscribeScheduler)
                    .observeOn(observerScheduler);
        }
    }
}
