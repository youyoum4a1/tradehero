package com.tradehero.th.network.retrofit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.CustomXmlConverter;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
import com.tradehero.th.fragments.settings.SettingsTransactionHistoryFragment;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.network.CompetitionUrl;
import com.tradehero.th.network.FriendlyUrlConnectionClient;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.network.service.*;
import com.tradehero.th.utils.RetrofitConstants;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.Server;
import retrofit.converter.Converter;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:39 AM Copyright (c) TradeHero
 */

@Module(
        injects = {
                SettingsTransactionHistoryFragment.class,
                SettingsPayPalFragment.class,
                ProviderPageIntent.class,
        },
        complete = false,
        library = true
)
public class RetrofitModule
{
    //<editor-fold desc="API Services">
    @Provides @Singleton UserService provideUserService(RestAdapter engine)
    {
        return engine.create(UserService.class);
    }

    @Provides @Singleton SessionService provideSessionService(RestAdapter engine)
    {
        return engine.create(SessionService.class);
    }

    @Provides @Singleton SecurityService provideSecurityService(RestAdapter engine)
    {
        return engine.create(SecurityService.class);
    }

    @Provides @Singleton UserTimelineService provideUserTimelineService(RestAdapter engine)
    {
        return engine.create(UserTimelineService.class);
    }

    @Provides @Singleton QuoteService provideQuoteService(RestAdapter engine)
    {
        return engine.create(QuoteService.class);
    }

    @Provides @Singleton PortfolioService providePortfolioService(RestAdapter engine)
    {
        return engine.create(PortfolioService.class);
    }

    @Provides @Singleton PositionService providePositionService(RestAdapter engine)
    {
        return engine.create(PositionService.class);
    }

    @Provides @Singleton TradeService provideTradeService(RestAdapter engine)
    {
        return engine.create(TradeService.class);
    }

    @Provides @Singleton LeaderboardService provideLeaderboardService(RestAdapter engine)
    {
        return engine.create(LeaderboardService.class);
    }

    @Provides @Singleton ProviderService provideProviderService(RestAdapter engine)
    {
        return engine.create(ProviderService.class);
    }

    @Provides @Singleton MarketService provideMarketService(RestAdapter engine)
    {
        return engine.create(MarketService.class);
    }

    @Provides @Singleton FollowerService provideFollowerService(RestAdapter engine)
    {
        return engine.create(FollowerService.class);
    }

    @Provides @Singleton AlertService provideAlertService(RestAdapter engine)
    {
        return engine.create(AlertService.class);
    }

    @Provides @Singleton AlertPlanService provideAlertPlanService(RestAdapter engine)
    {
        return engine.create(AlertPlanService.class);
    }

    @Provides @Singleton SocialService provideSocialService(RestAdapter engine)
    {
        return engine.create(SocialService.class);
    }

    @Provides @Singleton WatchlistService provideWatchlistService(RestAdapter engine)
    {
        return engine.create(WatchlistService.class);
    }

    @Provides @Singleton CompetitionService provideCompetitionService(RestAdapter engine)
    {
        return engine.create(CompetitionService.class);
    }
    //</editor-fold>

    @Provides @Singleton ObjectMapper provideObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // TODO confirm this is correct here
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        return objectMapper;
    }

    @Provides @Singleton Converter provideConverter(ObjectMapper objectMapper)
    {
        return new JacksonConverter(objectMapper);
    }

    @Provides @Singleton Server provideApiServer(@ServerEndpoint StringPreference serverEndpointPreference)
    {
        return new Server(serverEndpointPreference.get());
    }

    @Provides @Singleton @CompetitionUrl String provideCompetitionUrl(Server server)
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
                .setLogLevel(RetrofitConstants.DEFAULT_SERVICE_LOG_LEVEL);
    }

    @Provides @Singleton RestAdapter provideRestAdapter(RestAdapter.Builder builder, Server server, RequestHeaders requestHeaders)
    {
        return builder.setServer(server).setRequestInterceptor(requestHeaders).build();
    }

    @Provides @Singleton YahooNewsService provideYahooService(RestAdapter.Builder builder)
    {
        return builder.setServer(NetworkConstants.YAHOO_FINANCE_ENDPOINT).build().create(YahooNewsService.class);
    }

    @Provides @Singleton
    TranslationTokenService provideTranslationTokenService(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.TRANSLATION_REQ_TOKEN_ENDPOINT)
                .build().create(TranslationTokenService.class);
    }

    @Provides @Singleton
    TranslationService provideTranslationService(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.TRANSLATION_ENDPOINT)
                .setConverter(new CustomXmlConverter())
                .build().create(TranslationService.class);
    }
}
