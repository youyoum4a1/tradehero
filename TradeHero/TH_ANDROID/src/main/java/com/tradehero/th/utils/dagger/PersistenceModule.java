package com.tradehero.th.utils.dagger;

import android.app.Application;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.common.persistence.PersistableResource;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.TimelineManager;
import com.tradehero.th.persistence.TimelineStore;
import com.tradehero.th.persistence.UserManager;
import com.tradehero.th.persistence.UserStore;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 5:51 PM Copyright (c) TradeHero */

@Module(
        injects = {
                UserManager.class,
                TimelineManager.class,
                DatabaseCache.class,
                TimelineStore.Factory.class
        }
)
public class PersistenceModule
{
    private final Application application;

    public PersistenceModule(Application application) {
        this.application = application;
    }

    @Provides @Singleton PersistableResource<UserProfileDTO> provideUserStore()
    {
        return new UserStore();
    }

    @Provides @Singleton DatabaseCache provideDatabaseCache()
    {
        return new DatabaseCache();
    }

    @Provides @Singleton CacheHelper provideCacheHelper()
    {
        return new CacheHelper(application);
    }

    @Provides @Singleton TimelineStore.Factory provideTimelineStoreFactory()
    {
        return new TimelineStore.Factory();
    }

    @Provides TimelineStore provideTimelineStore()
    {
        return new TimelineStore();
    }
}
