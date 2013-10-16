package com.tradehero.th.utils;

import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.NetworkEngineTest;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.network.service.LeaderboardServiceTest;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 4:06 PM Copyright (c) TradeHero */
@Module(
        injects = {
                LeaderboardServiceTest.class
        }
)
public class TestModule
{
    private NetworkEngineTest networkEngine;

    public TestModule(NetworkEngineTest networkEngine)
    {
        this.networkEngine = networkEngine;
    }


    @Provides @Singleton LeaderboardService provideLeaderboardService()
    {
        return networkEngine.createService(LeaderboardService.class);
    }
}
