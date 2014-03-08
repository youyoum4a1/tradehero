package com.tradehero.th.network.service;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:39 AM Copyright (c) TradeHero
 */

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

    @Provides @Singleton NewsServiceAsync provideNewsServiceAsync(RestAdapter adapter)
    {
        return adapter.create(NewsServiceAsync.class);
    }

    @Provides @Singleton SocialServiceAsync provideSocialServiceAsync(RestAdapter adapter)
    {
        return adapter.create(SocialServiceAsync.class);
    }

    @Provides @Singleton DiscussionServiceAsync provideDiscussionServiceAsync(RestAdapter adapter)
    {
        return adapter.create(DiscussionServiceAsync.class);
    }
    //</editor-fold>
}
