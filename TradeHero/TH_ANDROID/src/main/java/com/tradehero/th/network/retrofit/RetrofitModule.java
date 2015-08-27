package com.tradehero.th.network.retrofit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.common.utils.RetrofitHelper;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.services.HengshengServiceAync;
import com.tradehero.th.api.competition.ProviderCompactDTO;
import com.tradehero.th.api.competition.ProviderCompactDTODeserialiser;
import com.tradehero.th.api.competition.ProviderCompactDTOJacksonModule;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTODeserialiser;
import com.tradehero.th.api.competition.ProviderDTOJacksonModule;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTODeserialiser;
import com.tradehero.th.api.position.PositionDTOJacksonModule;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTODeserialiser;
import com.tradehero.th.api.social.UserFriendsDTOJacksonModule;
import com.tradehero.th.network.CompetitionUrl;
import com.tradehero.th.network.FriendlyUrlConnectionClient;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.network.service.AlertService;
import com.tradehero.th.network.service.CompetitionService;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.network.service.FollowerService;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.network.service.MarketService;
import com.tradehero.th.network.service.MessageService;
import com.tradehero.th.network.service.NewsServiceSync;
import com.tradehero.th.network.service.NotificationService;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.PositionService;
import com.tradehero.th.network.service.ProviderService;
import com.tradehero.th.network.service.RetrofitProtectedModule;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.TradeService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.network.service.WatchlistService;
import com.tradehero.th.network.service.YahooNewsService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

@Module(
        includes = {
                RetrofitProtectedModule.class,
        },
        injects = {
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

    @Provides @Singleton
    HengshengServiceAync provideHengshengService(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.HENGSHENG_ENDPOINT).build().create(HengshengServiceAync.class);
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
