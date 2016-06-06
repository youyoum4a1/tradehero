package com.androidth.general.network.service;

import com.androidth.general.network.NetworkConstants;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;

@Module(
        includes = {
                RetrofitGameLiveProtectedModule.class,
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

    @Provides @Singleton UserTimelineServiceRx provideUserTimelineServiceRx(RestAdapter adapter)
    {
        return adapter.create(UserTimelineServiceRx.class);
    }

    @Provides @Singleton VideoServiceRx provideVideoServiceRx(RestAdapter adapter)
    {
        return adapter.create(VideoServiceRx.class);
    }

    @Provides @Singleton WatchlistServiceRx provideWatchlistServiceRx(RestAdapter adapter)
    {
        return adapter.create(WatchlistServiceRx.class);
    }

    @Provides @Singleton WeChatServiceRx provideWeChatServiceRx(RestAdapter adapter)
    {
        return adapter.create(WeChatServiceRx.class);
    }
    //</editor-fold>
}
