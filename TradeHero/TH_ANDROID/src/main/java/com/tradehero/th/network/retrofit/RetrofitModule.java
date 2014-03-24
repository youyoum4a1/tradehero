package com.tradehero.th.network.retrofit;

import android.content.Context;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
import com.tradehero.th.fragments.settings.SettingsTransactionHistoryFragment;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.network.CompetitionUrl;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.AlertService;
import com.tradehero.th.network.service.CompetitionService;
import com.tradehero.th.network.service.FollowerService;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.network.service.MarketService;
import com.tradehero.th.network.service.NewsServiceSync;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.PositionService;
import com.tradehero.th.network.service.ProviderService;
import com.tradehero.th.network.service.QuoteService;
import com.tradehero.th.network.service.RetrofitProtectedModule;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.TradeService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.network.service.WatchlistService;
import com.tradehero.th.network.service.YahooNewsService;
import com.tradehero.th.utils.NetworkUtils;
import com.tradehero.th.utils.RetrofitConstants;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.io.IOException;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.Server;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:39 AM Copyright (c) TradeHero
 */

@Module(
        includes = {
                RetrofitProtectedModule.class,
        },
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
    @Provides @Singleton NewsServiceSync provideNewServiceSync(RestAdapter adapter)
    {
        return adapter.create(NewsServiceSync.class);
    }
    
    @Provides @Singleton UserService provideUserService(RestAdapter adapter)
    {
        return adapter.create(UserService.class);
    }

    @Provides @Singleton SessionService provideSessionService(RestAdapter adapter)
    {
        return adapter.create(SessionService.class);
    }

    @Provides @Singleton SecurityService provideSecurityService(RestAdapter adapter)
    {
        return adapter.create(SecurityService.class);
    }

    @Provides @Singleton UserTimelineService provideUserTimelineService(RestAdapter adapter)
    {
        return adapter.create(UserTimelineService.class);
    }

    @Provides @Singleton QuoteService provideQuoteService(RestAdapter adapter)
    {
        return adapter.create(QuoteService.class);
    }

    @Provides @Singleton PortfolioService providePortfolioService(RestAdapter adapter)
    {
        return adapter.create(PortfolioService.class);
    }

    @Provides @Singleton PositionService providePositionService(RestAdapter adapter)
    {
        return adapter.create(PositionService.class);
    }

    @Provides @Singleton TradeService provideTradeService(RestAdapter adapter)
    {
        return adapter.create(TradeService.class);
    }

    @Provides @Singleton LeaderboardService provideLeaderboardService(RestAdapter adapter)
    {
        return adapter.create(LeaderboardService.class);
    }

    @Provides @Singleton ProviderService provideProviderService(RestAdapter adapter)
    {
        return adapter.create(ProviderService.class);
    }

    @Provides @Singleton MarketService provideMarketService(RestAdapter adapter)
    {
        return adapter.create(MarketService.class);
    }

    @Provides @Singleton FollowerService provideFollowerService(RestAdapter adapter)
    {
        return adapter.create(FollowerService.class);
    }

    @Provides @Singleton AlertService provideAlertService(RestAdapter adapter)
    {
        return adapter.create(AlertService.class);
    }

    @Provides @Singleton AlertPlanService provideAlertPlanService(RestAdapter adapter)
    {
        return adapter.create(AlertPlanService.class);
    }

    @Provides @Singleton SocialService provideSocialService(RestAdapter adapter)
    {
        return adapter.create(SocialService.class);
    }

    @Provides @Singleton WatchlistService provideWatchlistService(RestAdapter adapter)
    {
        return adapter.create(WatchlistService.class);
    }

    @Provides @Singleton CompetitionService provideCompetitionService(RestAdapter adapter)
    {
        return adapter.create(CompetitionService.class);
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

    @Provides @Singleton RestAdapter provideRestAdapter(RestAdapter.Builder builder, Server server, RequestHeaders requestHeaders)
    {
        return builder.setServer(server).setRequestInterceptor(requestHeaders).build();
    }

    @Provides @Singleton YahooNewsService provideYahooService(RestAdapter.Builder builder)
    {
        return builder.setServer(NetworkConstants.YAHOO_FINANCE_ENDPOINT).build().create(YahooNewsService.class);
    }

    @Provides Client provideOkClient(Context context)
    {
        File httpCacheDirectory = new File(context.getCacheDir(), "HttpCache");

        HttpResponseCache httpResponseCache = null;
        try
        {
            httpResponseCache = new HttpResponseCache(httpCacheDirectory, 10 * 1024);
        }
        catch (IOException e)
        {
            Timber.e("Could not create http cache", e);
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setResponseCache(httpResponseCache);
        okHttpClient.setSslSocketFactory(NetworkUtils.createBadSslSocketFactory());
        return new OkClient(okHttpClient);
    }
}
