package com.tradehero.th.network.service;

import dagger.Module;

@Module(
        injects = {
                ServiceWrapperTest.class,

                // These additions are here to help pass the tests
                AchievementServiceWrapper.class,
                AchievementMockServiceWrapper.class,
                AlertPlanServiceWrapper.class,
                AlertServiceWrapper.class,
                CompetitionServiceWrapper.class,
                CurrencyServiceWrapper.class,
                DiscussionServiceWrapper.class,
                FollowerServiceWrapper.class,
                HomeServiceWrapper.class,
                LeaderboardServiceWrapper.class,
                MarketServiceWrapper.class,
                MessageServiceWrapper.class,
                NewsServiceWrapper.class,
                NotificationServiceWrapper.class,
                PortfolioServiceWrapper.class,
                PositionServiceWrapper.class,
                ProviderServiceWrapper.class,
                QuoteServiceWrapper.class,
                SecurityServiceWrapper.class,
                SessionServiceWrapper.class,
                SocialServiceWrapper.class,
                TradeServiceWrapper.class,
                TranslationServiceBingWrapper.class,
                TranslationServiceWrapper.class,
                TranslationTokenServiceWrapper.class,
                UserServiceWrapper.class,
                UserTimelineMarkerServiceWrapper.class,
                UserTimelineServiceWrapper.class,
                VideoServiceWrapper.class,
                WatchlistServiceWrapper.class,
                WeChatServiceWrapper.class,
                YahooNewsServiceWrapper.class,
        },
        library = true,
        complete = false,
        overrides = true
)
public class NetworkServiceTestModule
{
}
