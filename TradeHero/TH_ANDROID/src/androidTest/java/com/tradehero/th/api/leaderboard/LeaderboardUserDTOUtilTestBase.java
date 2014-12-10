package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

abstract public class LeaderboardUserDTOUtilTestBase
{
    protected StocksLeaderboardUserDTO getUser1()
    {
        StocksLeaderboardUserDTO value = new StocksLeaderboardUserDTO();
        value.id = 11;
        value.lbmuId = 21;
        return value;
    }

    protected StocksLeaderboardUserDTO getUser2()
    {
        StocksLeaderboardUserDTO value = new StocksLeaderboardUserDTO();
        value.id = 12;
        value.lbmuId = 22;
        return value;
    }

    protected List<StocksLeaderboardUserDTO> getListEmpty()
    {
        return new ArrayList<>();
    }

    protected Map<LeaderboardUserId, StocksLeaderboardUserDTO> getMapEmpty()
    {
        return new HashMap<>();
    }

    protected List<StocksLeaderboardUserDTO> getList1Item()
    {
        List<StocksLeaderboardUserDTO> list = new ArrayList<>();
        list.add(getUser1());
        return list;
    }

    protected List<StocksLeaderboardUserDTO> getList2Items()
    {
        List<StocksLeaderboardUserDTO> list = new ArrayList<>();
        list.add(getUser1());
        list.add(getUser2());
        return list;
    }

    protected Map<LeaderboardUserId, StocksLeaderboardUserDTO> getMap1Item()
    {
        Map<LeaderboardUserId, StocksLeaderboardUserDTO> map = new HashMap<>();
        map.put(new LeaderboardUserId(11, 21l), getUser1());
        return map;
    }

    protected Map<LeaderboardUserId, StocksLeaderboardUserDTO> getMap2Items()
    {
        Map<LeaderboardUserId, StocksLeaderboardUserDTO> map = new HashMap<>();
        map.put(new LeaderboardUserId(11, 21l), getUser1());
        map.put(new LeaderboardUserId(12, 22l), getUser2());
        return map;
    }

    protected void assertEmpty(Map<LeaderboardUserId, StocksLeaderboardUserDTO> map)
    {
        assertEquals(0, map.size());
    }

    protected void assertMap1Item(Map<LeaderboardUserId, StocksLeaderboardUserDTO> map)
    {
        assertEquals(1, map.size());
        StocksLeaderboardUserDTO value = map.get(new LeaderboardUserId(11, 21l));
        assertEquals(11, value.id);
        assertEquals(21, value.lbmuId);
    }

    protected void assertMap2Items(Map<LeaderboardUserId, StocksLeaderboardUserDTO> map)
    {
        assertEquals(2, map.size());
        StocksLeaderboardUserDTO value = map.get(new LeaderboardUserId(11, 21l));
        assertEquals(11, value.id);
        assertEquals(21, value.lbmuId);
        value = map.get(new LeaderboardUserId(12, 22l));
        assertEquals(12, value.id);
        assertEquals(22, value.lbmuId);
    }
}
