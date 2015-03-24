package com.tradehero.th.network.service;

import com.tradehero.common.utils.CustomXmlConverter;
import com.tradehero.th.network.NetworkConstants;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

import javax.inject.Singleton;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class RetrofitProtectedModule
{

    @Provides @Singleton CompetitionServiceAsync provideCompetitionService(RestAdapter adapter)
    {
        return adapter.create(CompetitionServiceAsync.class);
    }

    @Provides @Singleton DiscussionServiceAsync provideDiscussionServiceAsync(RestAdapter adapter)
    {
        return adapter.create(DiscussionServiceAsync.class);
    }

    @Provides @Singleton MarketServiceAsync provideMarketServiceAsync(RestAdapter adapter)
    {
        return adapter.create(MarketServiceAsync.class);
    }

    @Provides @Singleton MessageServiceAsync provideMessageServiceAsync(RestAdapter adapter)
    {
        return adapter.create(MessageServiceAsync.class);
    }

    @Provides @Singleton NewsServiceAsync provideNewsServiceAsync(RestAdapter adapter)
    {
        return adapter.create(NewsServiceAsync.class);
    }

    @Provides @Singleton NotificationServiceAsync provideNotificationServiceAsync(RestAdapter adapter)
    {
        return adapter.create(NotificationServiceAsync.class);
    }

    @Provides @Singleton PositionServiceAsync providePositionServiceAsync(RestAdapter adapter)
    {
        return adapter.create(PositionServiceAsync.class);
    }

    @Provides @Singleton ProviderServiceAsync provideProviderServiceAsync(RestAdapter adapter)
    {
        return adapter.create(ProviderServiceAsync.class);
    }

    @Provides @Singleton QuoteServiceAsync provideQuoteServiceAsync(RestAdapter adapter)
    {
        return adapter.create(QuoteServiceAsync.class);
    }

    @Provides @Singleton SecurityServiceAsync provideSecurityServiceAsync(RestAdapter adapter)
    {
        return adapter.create(SecurityServiceAsync.class);
    }

    @Provides @Singleton SessionServiceAsync provideSessionServiceAsync(RestAdapter adapter)
    {
        return adapter.create(SessionServiceAsync.class);
    }

    @Provides @Singleton SocialServiceAsync provideSocialServiceAsync(RestAdapter adapter)
    {
        return adapter.create(SocialServiceAsync.class);
    }


    @Provides @Singleton AdministratorManageTimelineServiceAsync provideAdministratorManageTimelineServiceAsync(RestAdapter adapter)
    {
        return adapter.create(AdministratorManageTimelineServiceAsync.class);
    }

    @Provides @Singleton TranslationServiceBingAsync provideBingTranslationServiceAsync(RestAdapter.Builder builder)
    {
        return builder.setEndpoint(NetworkConstants.BING_TRANSLATION_ENDPOINT)
                .setConverter(new CustomXmlConverter())
                .build().create(TranslationServiceBingAsync.class);
    }

    @Provides @Singleton UserServiceAsync provideUserService(RestAdapter adapter)
    {
        return adapter.create(UserServiceAsync.class);
    }

    @Provides @Singleton ShareServiceAsync provideShareService(RestAdapter adapter)
    {
        return adapter.create(ShareServiceAsync.class);
    }

    @Provides @Singleton UserTimelineServiceAsync provideUserTimelineServiceAsync(RestAdapter adapter)
    {
        return adapter.create(UserTimelineServiceAsync.class);
    }

    @Provides @Singleton WatchlistServiceAsync provideWatchlistServiceAsync(RestAdapter adapter)
    {
        return adapter.create(WatchlistServiceAsync.class);
    }

    @Provides @Singleton WeChatServiceAsync provideWeChatServiceAsync(RestAdapter adapter)
    {
        return adapter.create(WeChatServiceAsync.class);
    }

}
