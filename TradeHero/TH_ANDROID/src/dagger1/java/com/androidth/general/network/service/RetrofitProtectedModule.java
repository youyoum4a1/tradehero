package com.androidth.general.network.service;

import com.androidth.general.network.NetworkConstants;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

import javax.inject.Singleton;

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
    @Provides @Singleton AchievementServiceRx provideAchievementServiceRx(Retrofit adapter)
    {
        return adapter.create(AchievementServiceRx.class);
    }

    @Provides @Singleton AlertPlanServiceRx provideAlertPlanServiceRx(Retrofit adapter)
    {
        return adapter.create(AlertPlanServiceRx.class);
    }

    @Provides @Singleton AlertServiceRx provideAlertServiceRx(Retrofit adapter)
    {
        return adapter.create(AlertServiceRx.class);
    }

    @Provides @Singleton CompetitionServiceRx provideCompetitionServiceRx(Retrofit adapter)
    {
        return adapter.create(CompetitionServiceRx.class);
    }

    @Provides @Singleton CurrencyServiceRx provideCurrencyServiceRx(Retrofit adapter)
    {
        return adapter.create(CurrencyServiceRx.class);
    }

    @Provides @Singleton DiscussionServiceRx provideDiscussionServiceRx(Retrofit adapter)
    {
        return adapter.create(DiscussionServiceRx.class);
    }

    @Provides @Singleton FollowerServiceRx provideFollowerServiceRx(Retrofit adapter)
    {
        return adapter.create(FollowerServiceRx.class);
    }

    @Provides @Singleton LeaderboardServiceRx provideLeaderboardServiceRx(Retrofit adapter)
    {
        return adapter.create(LeaderboardServiceRx.class);
    }

    @Provides @Singleton MarketServiceRx provideMarketServiceRx(Retrofit adapter)
    {
        return adapter.create(MarketServiceRx.class);
    }

    @Provides @Singleton MessageServiceRx provideMessageServiceRx(Retrofit adapter)
    {
        return adapter.create(MessageServiceRx.class);
    }

    @Provides @Singleton NewsServiceRx provideNewsServiceRx(Retrofit adapter)
    {
        return adapter.create(NewsServiceRx.class);
    }

    @Provides @Singleton NotificationServiceRx provideNotificationServiceRx(Retrofit adapter)
    {
        return adapter.create(NotificationServiceRx.class);
    }

    @Provides @Singleton PortfolioServiceRx providePortfolioServiceRx(Retrofit adapter)
    {
        return adapter.create(PortfolioServiceRx.class);
    }

    @Provides @Singleton PositionServiceRx providePositionServiceRx(Retrofit adapter)
    {
        return adapter.create(PositionServiceRx.class);
    }

    @Provides @Singleton ProviderServiceRx provideProviderServiceRx(Retrofit adapter)
    {
        return adapter.create(ProviderServiceRx.class);
    }

    @Provides @Singleton QuoteServiceRx provideQuoteServiceRx(Retrofit adapter)
    {
        return adapter.create(QuoteServiceRx.class);
    }

    @Provides @Singleton SecurityServiceRx provideSecurityServiceRx(Retrofit adapter)
    {
        return adapter.create(SecurityServiceRx.class);
    }

    @Provides @Singleton SessionServiceRx provideSessionServiceRx(Retrofit adapter)
    {
        return adapter.create(SessionServiceRx.class);
    }

    @Provides @Singleton SocialServiceRx provideSocialServiceRx(Retrofit adapter)
    {
        return adapter.create(SocialServiceRx.class);
    }

    @Provides @Singleton TradeServiceRx provideTradeServiceRx(Retrofit adapter)
    {
        return adapter.create(TradeServiceRx.class);
    }

    @Provides @Singleton TranslationServiceBingRx provideBingTranslationServiceRx(Retrofit.Builder builder)
    {
        return builder
//                .setEndpoint(NetworkConstants.BING_TRANSLATION_ENDPOINT)
                .baseUrl(NetworkConstants.BING_TRANSLATION_ENDPOINT)
                .build().create(TranslationServiceBingRx.class);
    }

    @Provides @Singleton TranslationTokenServiceRx provideTranslationTokenServiceRx(Retrofit adapter)
    {
        return adapter.create(TranslationTokenServiceRx.class);
    }

    @Provides @Singleton UserServiceRx provideUserServiceRx(Retrofit adapter)
    {
        return adapter.create(UserServiceRx.class);
    }

    @Provides @Singleton UserTimelineServiceRx provideUserTimelineServiceRx(Retrofit adapter)
    {
        return adapter.create(UserTimelineServiceRx.class);
    }

    @Provides @Singleton VideoServiceRx provideVideoServiceRx(Retrofit adapter)
    {
        return adapter.create(VideoServiceRx.class);
    }

    @Provides @Singleton WatchlistServiceRx provideWatchlistServiceRx(Retrofit adapter)
    {
        return adapter.create(WatchlistServiceRx.class);
    }

    @Provides @Singleton WeChatServiceRx provideWeChatServiceRx(Retrofit adapter)
    {
        return adapter.create(WeChatServiceRx.class);
    }
    //</editor-fold>
}
