package com.tradehero.th.network.service;

import com.tradehero.common.utils.CustomXmlConverter;
import com.tradehero.th.network.NetworkConstants;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
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
    @Provides @Singleton UserServiceAsync provideUserService(RestAdapter adapter)
    {
        return adapter.create(UserServiceAsync.class);
    }

    @Provides @Singleton AlertServiceAsync provideAlertService(RestAdapter adapter)
    {
        return adapter.create(AlertServiceAsync.class);
    }

    @Provides @Singleton CompetitionServiceAsync provideCompetitionService(RestAdapter adapter)
    {
        return adapter.create(CompetitionServiceAsync.class);
    }

    @Provides @Singleton FollowerServiceAsync provideFollowerService(RestAdapter adapter)
    {
        return adapter.create(FollowerServiceAsync.class);
    }

    @Provides @Singleton LeaderboardServiceAsync provideLeaderboardService(RestAdapter adapter)
    {
        return adapter.create(LeaderboardServiceAsync.class);
    }

    @Provides @Singleton NewsServiceAsync provideNewsServiceAsync(RestAdapter adapter)
    {
        return adapter.create(NewsServiceAsync.class);
    }

    @Provides @Singleton PortfolioServiceAsync providePortfolioServiceAsync(RestAdapter adapter)
    {
        return adapter.create(PortfolioServiceAsync.class);
    }

    @Provides @Singleton PositionServiceAsync providePositionServiceAsync(RestAdapter adapter)
    {
        return adapter.create(PositionServiceAsync.class);
    }

    @Provides @Singleton ProviderServiceAsync provideProviderServiceAsync(RestAdapter adapter)
    {
        return adapter.create(ProviderServiceAsync.class);
    }

    @Provides @Singleton SocialServiceAsync provideSocialServiceAsync(RestAdapter adapter)
    {
        return adapter.create(SocialServiceAsync.class);
    }

    @Provides @Singleton DiscussionServiceAsync provideDiscussionServiceAsync(RestAdapter adapter)
    {
        return adapter.create(DiscussionServiceAsync.class);
    }

    @Provides @Singleton MessageServiceAsync provideMessageServiceAsync(RestAdapter adapter)
    {
        return adapter.create(MessageServiceAsync.class);
    }

    @Provides @Singleton NotificationServiceAsync provideNotificationServiceAsync(RestAdapter adapter)
    {
        return adapter.create(NotificationServiceAsync.class);
    }

    @Provides @Singleton TradeServiceAsync provideTradeServiceAsync(RestAdapter adapter)
    {
        return adapter.create(TradeServiceAsync.class);
    }

    @Provides @Singleton TranslationTokenServiceAsync provideTranslationTokenServiceAsync(RestAdapter adapter)
    {
        return adapter.create(TranslationTokenServiceAsync.class);
    }

    @Provides @Singleton BingTranslationServiceAsync provideBingTranslationServiceAsync(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.BING_TRANSLATION_ENDPOINT)
                .setConverter(new CustomXmlConverter())
                .build().create(BingTranslationServiceAsync.class);
    }

    @Provides @Singleton UserTimelineServiceAsync provideUserTimelineServiceAsync(RestAdapter adapter)
    {
        return adapter.create(UserTimelineServiceAsync.class);
    }

    @Provides @Singleton WatchlistServiceAsync provideWatchlistServiceAsync(RestAdapter adapter)
    {
        return adapter.create(WatchlistServiceAsync.class);
    }
    //</editor-fold>
}
