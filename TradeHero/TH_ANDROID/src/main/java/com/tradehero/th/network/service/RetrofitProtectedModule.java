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
    @Provides @Singleton UserServiceProtected provideUserServiceProtected(RestAdapter engine)
    {
        return engine.create(UserServiceProtected.class);
    }

    @Provides @Singleton AlertServiceProtected provideAlertServiceProtected(RestAdapter engine)
    {
        return engine.create(AlertServiceProtected.class);
    }
    //</editor-fold>
}
