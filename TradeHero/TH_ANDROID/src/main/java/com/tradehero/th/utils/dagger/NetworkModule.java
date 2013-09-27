package com.tradehero.th.utils.dagger;

import com.tradehero.th.api.form.UserAvailabilityRequester;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.loaders.SearchStockPageItemListLoader;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.persistence.TimelineStore;
import com.tradehero.th.persistence.UserStore;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:36 PM Copyright (c) TradeHero */
@Module(
        injects = {
                UserStore.class,
                TimelineStore.class,
                TrendingFragment.class,
                UserAvailabilityRequester.class,
                SearchStockPeopleFragment.class,
                SearchStockPageItemListLoader.class,
                EmailSignInFragment.class
        },
        staticInjections = {
                THUser.class
        }
)
public class NetworkModule
{
    private final NetworkEngine engine;

    public NetworkModule(NetworkEngine engine)
    {
        this.engine = engine;
    }

    @Provides
    @Singleton
    UserService provideUserService()
    {
        return engine.createService(UserService.class);
    }

    @Provides
    @Singleton
    SecurityService provideSecurityService()
    {
        return engine.createService(SecurityService.class);
    }

    @Provides
    @Singleton
    UserTimelineService provideUserTimelineService()
    {
        return engine.createService(UserTimelineService.class);
    }
}
