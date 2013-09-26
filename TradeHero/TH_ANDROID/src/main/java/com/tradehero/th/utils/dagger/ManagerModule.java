package com.tradehero.th.utils.dagger;

import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.persistence.TimelineManager;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 7:18 PM Copyright (c) TradeHero */
@Module(
        injects = TimelinePagedItemListLoader.class
)
public class ManagerModule
{
    @Provides @Singleton TimelineManager provideTimelineManager()
    {
        return new TimelineManager();
    }
}
