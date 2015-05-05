package com.tradehero.th.network.retrofit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.CustomXmlConverter;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.common.utils.RetrofitHelper;
import com.tradehero.th.api.competition.*;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTODeserialiser;
import com.tradehero.th.api.position.PositionDTOJacksonModule;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTODeserialiser;
import com.tradehero.th.api.social.UserFriendsDTOJacksonModule;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.network.CompetitionUrl;
import com.tradehero.th.network.FriendlyUrlConnectionClient;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.network.service.*;
import com.tradehero.th.widget.VotePair;
import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

import javax.inject.Singleton;

@Module(
        includes = {
                RetrofitProtectedModule.class,
        },
        injects = {
                ProviderPageIntent.class,

                VotePair.class
        },
        complete = false,
        library = true
)
public class RetrofitModule
{

    @Provides @Singleton AlertService provideAlertService(RestAdapter adapter)
    {
        return adapter.create(AlertService.class);
    }

    @Provides @Singleton CompetitionService provideCompetitionService(RestAdapter adapter)
    {
        return adapter.create(CompetitionService.class);
    }

    @Provides @Singleton DiscussionService provideDiscussionServiceSync(RestAdapter adapter)
    {
        return adapter.create(DiscussionService.class);
    }

    @Provides @Singleton FollowerService provideFollowerService(RestAdapter adapter)
    {
        return adapter.create(FollowerService.class);
    }

    @Provides @Singleton LeaderboardService provideLeaderboardService(RestAdapter adapter)
    {
        return adapter.create(LeaderboardService.class);
    }

    @Provides @Singleton MarketService provideMarketService(RestAdapter adapter)
    {
        return adapter.create(MarketService.class);
    }

    @Provides @Singleton MessageService provideMessageService(RestAdapter adapter)
    {
        return adapter.create(MessageService.class);
    }

    @Provides @Singleton NewsServiceSync provideNewServiceSync(RestAdapter adapter)
    {
        return adapter.create(NewsServiceSync.class);
    }

    @Provides @Singleton NotificationService provideNotificationService(RestAdapter adapter)
    {
        return adapter.create(NotificationService.class);
    }

    @Provides @Singleton PortfolioService providePortfolioService(RestAdapter adapter)
    {
        return adapter.create(PortfolioService.class);
    }

    @Provides @Singleton PositionService providePositionService(RestAdapter adapter)
    {
        return adapter.create(PositionService.class);
    }

    @Provides @Singleton ProviderService provideProviderService(RestAdapter adapter)
    {
        return adapter.create(ProviderService.class);
    }

    @Provides @Singleton SecurityService provideSecurityService(RestAdapter adapter)
    {
        return adapter.create(SecurityService.class);
    }

    @Provides @Singleton SessionService provideSessionService(RestAdapter adapter)
    {
        return adapter.create(SessionService.class);
    }

    @Provides @Singleton TradeService provideTradeService(RestAdapter adapter)
    {
        return adapter.create(TradeService.class);
    }

    @Provides @Singleton TranslationServiceBing provideBingTranslationService(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.BING_TRANSLATION_ENDPOINT)
                .setConverter(null)
                .build().create(TranslationServiceBing.class);
    }

    @Provides @Singleton TranslationTokenService provideTranslationTokenService(RestAdapter adapter)
    {
        return adapter.create(TranslationTokenService.class);
    }

    @Provides @Singleton UserService provideUserService(RestAdapter adapter)
    {
        return adapter.create(UserService.class);
    }

    @Provides @Singleton UserTimelineService provideUserTimelineService(RestAdapter adapter)
    {
        return adapter.create(UserTimelineService.class);
    }

    @Provides @Singleton WatchlistService provideWatchlistService(RestAdapter adapter)
    {
        return adapter.create(WatchlistService.class);
    }

    @Provides @Singleton YahooNewsService provideYahooService(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.YAHOO_FINANCE_ENDPOINT).build().create(YahooNewsService.class);
    }


    @Provides JsonDeserializer<PositionDTO> providesPositionDTODeserialiser(PositionDTODeserialiser deserialiser)
    {
        return deserialiser;
    }

    @Provides JsonDeserializer<ProviderCompactDTO> providesProviderCompactDTODeserialiser(ProviderCompactDTODeserialiser deserialiser)
    {
        return deserialiser;
    }

    @Provides JsonDeserializer<ProviderDTO> providesProviderDTODeserialiser(ProviderDTODeserialiser deserialiser)
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
        return objectMapper;
    }

    @Provides @Singleton @ForApp ObjectMapper provideObjectMapper(
            ObjectMapper objectMapper,
            UserFriendsDTOJacksonModule userFriendsDTOModule,
            PositionDTOJacksonModule positionDTOModule,
            ProviderCompactDTOJacksonModule providerCompactDTOModule,
            ProviderDTOJacksonModule providerDTOModule)
    {
        objectMapper.registerModule(userFriendsDTOModule);
        objectMapper.registerModule(positionDTOModule);
        objectMapper.registerModule(providerCompactDTOModule);
        objectMapper.registerModule(providerDTOModule);

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
            FriendlyUrlConnectionClient client,
            Converter converter,
            RetrofitSynchronousErrorHandler errorHandler)
    {
        return new RestAdapter.Builder()
                .setConverter(converter)
                .setClient(client)
                .setErrorHandler(errorHandler)
                .setLogLevel(RetrofitHelper.DEFAULT_SERVICE_LOG_LEVEL);
    }

    @Provides @Singleton RestAdapter provideRestAdapter(RestAdapter.Builder builder, Endpoint server, RequestHeaders requestHeaders)
    {
        return builder.setEndpoint(server).setRequestInterceptor(requestHeaders).build();
    }

}
