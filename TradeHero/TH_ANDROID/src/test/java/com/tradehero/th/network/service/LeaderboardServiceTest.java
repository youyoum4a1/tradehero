package com.tradehero.th.network.service;

import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.NetworkEngineTest;
import com.tradehero.th.utils.TestModule;
import dagger.ObjectGraph;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 3:50 PM Copyright (c) TradeHero */
@RunWith(RobolectricTestRunner.class)
public class LeaderboardServiceTest
{
    @Inject LeaderboardService leaderboardService;

    @Before
    public void setUp()
    {
        ObjectGraph objectGraph = ObjectGraph.create(new TestModule(NetworkEngineTest.getInstance()));
        objectGraph.inject(this);
    }

    @Test
    public void getLeaderboardDefinitions_shouldReturnANonEmptyList()
    {
        List<LeaderboardDefDTO> leaderboardDefDTOs = leaderboardService.getLeaderboardDefinitions();
        assertThat(leaderboardDefDTOs.size()).isGreaterThan(0);
    }
}
