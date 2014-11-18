package com.tradehero.th.network.service;

import com.tradehero.common.utils.CustomXmlConverter;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.network.retrofit.RequestHeaders;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class RetrofitProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides @Singleton AchievementServiceRx provideAchievementServiceRx(RestAdapter adapter)
    {
        return adapter.create(AchievementServiceRx.class);
    }

    @Provides @Singleton AlertPlanServiceRx provideAlertPlanServiceRx(RestAdapter adapter)
    {
        return adapter.create(AlertPlanServiceRx.class);
    }

    @Provides @Singleton AlertServiceRx provideAlertServiceRx(RestAdapter adapter)
    {
        return adapter.create(AlertServiceRx.class);
    }

    @Provides @Singleton CompetitionServiceRx provideCompetitionServiceRx(RestAdapter adapter)
    {
        return adapter.create(CompetitionServiceRx.class);
    }

    @Provides @Singleton CurrencyServiceRx provideCurrencyServiceRx(RestAdapter adapter)
    {
        return adapter.create(CurrencyServiceRx.class);
    }

    @Provides @Singleton DiscussionServiceRx provideDiscussionServiceRx(RestAdapter adapter)
    {
        return adapter.create(DiscussionServiceRx.class);
    }

    @Provides @Singleton FollowerServiceRx provideFollowerServiceRx(RestAdapter adapter)
    {
        return adapter.create(FollowerServiceRx.class);
    }

    @Provides @Singleton LeaderboardServiceRx provideLeaderboardServiceRx(RestAdapter adapter)
    {
        return adapter.create(LeaderboardServiceRx.class);
    }

    @Provides @Singleton MarketServiceRx provideMarketServiceRx(RestAdapter adapter)
    {
        return adapter.create(MarketServiceRx.class);
    }

    @Provides @Singleton MessageServiceRx provideMessageServiceRx(RestAdapter adapter)
    {
        return adapter.create(MessageServiceRx.class);
    }

    @Provides @Singleton NewsServiceRx provideNewsServiceRx(RestAdapter adapter)
    {
        return adapter.create(NewsServiceRx.class);
    }

    @Provides @Singleton NotificationServiceRx provideNotificationServiceRx(RestAdapter adapter)
    {
        return adapter.create(NotificationServiceRx.class);
    }

    @Provides @Singleton PortfolioServiceRx providePortfolioServiceRx(RestAdapter adapter)
    {
        return adapter.create(PortfolioServiceRx.class);
    }

    @Provides @Singleton PositionServiceRx providePositionServiceRx(RestAdapter adapter)
    {
        return adapter.create(PositionServiceRx.class);
    }

    @Provides @Singleton ProviderServiceRx provideProviderServiceRx(RestAdapter adapter)
    {
        return adapter.create(ProviderServiceRx.class);
    }

    @Provides @Singleton QuoteServiceRx provideQuoteServiceRx(RestAdapter adapter)
    {
        return adapter.create(QuoteServiceRx.class);
    }

    @Provides @Singleton SecurityServiceAsync provideSecurityServiceAsync(RestAdapter adapter)
    {
        return adapter.create(SecurityServiceAsync.class);
    }

    @Provides @Singleton SecurityServiceRx provideSecurityServiceRx(RestAdapter adapter)
    {
        return adapter.create(SecurityServiceRx.class);
    }

    @Provides @Singleton SessionServiceRx provideSessionServiceRx(RestAdapter adapter)
    {
        return adapter.create(SessionServiceRx.class);
    }

    @Provides @Singleton SocialServiceRx provideSocialServiceRx(RestAdapter adapter)
    {
        return adapter.create(SocialServiceRx.class);
    }

    @Provides @Singleton TradeServiceRx provideTradeServiceRx(RestAdapter adapter)
    {
        return adapter.create(TradeServiceRx.class);
    }

    @Provides @Singleton TranslationServiceBingRx provideBingTranslationServiceRx(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.BING_TRANSLATION_ENDPOINT)
                .setConverter(new CustomXmlConverter())
                .build().create(TranslationServiceBingRx.class);
    }

    @Provides @Singleton TranslationTokenServiceRx provideTranslationTokenServiceRx(RestAdapter adapter)
    {
        return adapter.create(TranslationTokenServiceRx.class);
    }

    @Provides @Singleton UserServiceRx provideUserServiceRx(RestAdapter adapter)
    {
        return adapter.create(UserServiceRx.class);
    }

    @Provides @Singleton UserTimelineMarkerServiceRx provideUserTimelineMarkerServiceRx(RestAdapter adapter)
    {
        return adapter.create(UserTimelineMarkerServiceRx.class);
    }

    @Provides @Singleton UserTimelineServiceRx provideUserTimelineServiceRx(RestAdapter adapter)
    {
        return adapter.create(UserTimelineServiceRx.class);
    }

    @Provides @Singleton VideoServiceRx provideVideoServiceRx(RestAdapter adapter)
    {
        return adapter.create(VideoServiceRx.class);
    }

    @Provides @Singleton GamesServiceRx provideGamesServiceRx(RestAdapter adapter)
    {
        return adapter.create(GamesServiceRx.class);
    }

    @Provides @Singleton WatchlistServiceRx provideWatchlistServiceRx(RestAdapter adapter)
    {
        return adapter.create(WatchlistServiceRx.class);
    }

    @Provides @Singleton WeChatServiceRx provideWeChatServiceRx(RestAdapter adapter)
    {
        return adapter.create(WeChatServiceRx.class);
    }

    @Provides @Singleton YahooNewsServiceRx provideYahooServiceRx(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.YAHOO_FINANCE_ENDPOINT).build().create(YahooNewsServiceRx.class);
    }

    @Provides @Singleton HomeServiceRx provideHomeServiceRx(RestAdapter.Builder builder, RequestHeaders requestHeaders)
    {
        return builder.setEndpoint(NetworkConstants.TRADEHERO_PROD_ENDPOINT)
                .setRequestInterceptor(requestHeaders)
                .build()
                .create(HomeServiceRx.class);
    }
    //</editor-fold>
}
