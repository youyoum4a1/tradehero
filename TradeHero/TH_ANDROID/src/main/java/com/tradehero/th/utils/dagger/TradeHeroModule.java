package com.tradehero.th.utils.dagger;

import android.app.Application;
import android.content.Context;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.th.api.form.UserAvailabilityRequester;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.loaders.SearchStockPageItemListLoader;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.persistence.TimelineManager;
import com.tradehero.th.persistence.TimelineStore;
import com.tradehero.th.persistence.position.AbstractSecurityPositionDetailStore;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.AbstractSecurityCompactStore;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactStore;
import com.tradehero.th.persistence.position.SecurityPositionDetailStore;
import com.tradehero.th.persistence.user.AbstractUserStore;
import com.tradehero.th.persistence.user.UserManager;
import com.tradehero.th.persistence.user.UserStore;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:36 PM Copyright (c) TradeHero */
@Module(
        injects =
        {
                EmailSignInFragment.class,
                TrendingFragment.class,
                SearchStockPeopleFragment.class,
                TradeFragment.class,
                TimelineFragment.class,
                MeTimelineFragment.class,
                MarkdownTextView.class,

                TrendingFragment.class,

                UserAvailabilityRequester.class,
                SearchStockPageItemListLoader.class,
                TimelinePagedItemListLoader.class,

                UserManager.class,
                TimelineManager.class,

                UserStore.class,
                TimelineStore.class,
                TimelineStore.Factory.class,
                //SecurityCompactStore.class,
                //SecurityPositionDetailStore.class,
                SecurityCompactCache.class,
                SecurityCompactListCache.class,
                SecurityPositionDetailCache.class,

                DatabaseCache.class,
                CacheHelper.class
        },
        staticInjections =
        {
                THUser.class
        }
)
public class TradeHeroModule
{
    private final Application application;
    private final NetworkEngine engine;

    public TradeHeroModule(NetworkEngine engine, Application application)
    {
        this.application = application;
        this.engine = engine;
    }

    @Provides @Singleton UserService provideUserService()
    {
        return engine.createService(UserService.class);
    }

    @Provides @Singleton SecurityService provideSecurityService()
    {
        return engine.createService(SecurityService.class);
    }

    @Provides @Singleton UserTimelineService provideUserTimelineService()
    {
        return engine.createService(UserTimelineService.class);
    }

    @Provides @Singleton AbstractUserStore provideUserStore(UserStore store)
    {
        return store;
    }

    @Provides @Singleton AbstractSecurityPositionDetailStore providePositionDetailStore(SecurityPositionDetailStore store)
    {
        return store;
    }

    @Provides @Singleton AbstractSecurityCompactStore provideCompactStore(SecurityCompactStore store)
    {
        return store;
    }

    @Provides Context provideContext()
    {
        return application.getApplicationContext();
    }
}
