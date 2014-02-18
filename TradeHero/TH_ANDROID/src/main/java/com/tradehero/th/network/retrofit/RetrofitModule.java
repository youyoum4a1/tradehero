package com.tradehero.th.network.retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
import com.tradehero.th.fragments.settings.SettingsTransactionHistoryFragment;
import com.tradehero.th.network.CompetitionUrl;
import com.tradehero.th.network.FriendlyUrlConnectionClient;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.AlertService;
import com.tradehero.th.network.service.CompetitionService;
import com.tradehero.th.network.service.FollowerService;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.network.service.MarketService;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.PositionService;
import com.tradehero.th.network.service.ProviderService;
import com.tradehero.th.network.service.QuoteService;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.TradeService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.network.service.WatchlistService;
import com.tradehero.th.network.service.YahooNewsService;
import com.tradehero.th.utils.Constants;
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
        },
        complete = false,
        library = true
)
public class RetrofitModule
{
    //public static final String BASE_TH_URL = "http://192.168.1.64:1857/";
    //public static final String BASE_TH_URL = "https://192.168.1.64:44301/";
    //public static final String BASE_TH_URL = "http://truongtho.noip.me/";
    //public static final String BASE_TH_URL = "https://www.tradehero.mobi/";
    //public static final String BASE_TH_URL = "https://www.tradehero.mobi/";
    //public static final String BASE_API_URL = BASE_TH_URL + "api/";
    //public static final String PRIVACY_TERMS_OF_SERVICE = BASE_TH_URL + "privacy";

    private static final String YAHOO_FINANCE_ENDPOINT = "http://finance.yahoo.com";
    //private static final String TRADEHERO_DEV_ENDPOINT = "http://truongtho.noip.me/api/";
    private static final String TRADEHERO_DEV_ENDPOINT = "https://th-paas-test-dev1.cloudapp.net/api/";
    private static final String TRADEHERO_PROD_ENDPOINT = "https://www.tradehero.mobi/api/";
    private static final String COMPETITION_PATH = "competitionpages/";

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

    @Provides @Singleton Converter provideConverter()
    {
        return new JacksonConverter(new ObjectMapper());
    }

    @Provides @Singleton Server provideApiServer()
    {
        if (Constants.RELEASE)
        {
            return new Server(TRADEHERO_PROD_ENDPOINT);
        }
        else
        {
            return new Server(TRADEHERO_DEV_ENDPOINT);
        }
    }

    @Provides @Singleton @CompetitionUrl String provideCompetitionUrl(Server server)
    {
        return server.getUrl() + COMPETITION_PATH;
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
        return builder.setServer(YAHOO_FINANCE_ENDPOINT).build().create(YahooNewsService.class);
    }
}
