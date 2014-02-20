package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by xavier on 2/12/14.
 */
abstract public class BaseLeaderboardUserDTOUtilTest
{
    public static final String TAG = BaseLeaderboardUserDTOUtilTest.class.getSimpleName();

    protected LeaderboardUserDTO getUser1()
    {
        LeaderboardUserDTO value = new LeaderboardUserDTO();
        value.id = 11;
        value.lbmuId = 21;
        return value;
    }

    protected LeaderboardUserDTO getUser2()
    {
        LeaderboardUserDTO value = new LeaderboardUserDTO();
        value.id = 12;
        value.lbmuId = 22;
        return value;
    }

    protected List<LeaderboardUserDTO> getListEmpty()
    {
        return new ArrayList<>();
    }

    protected Map<LeaderboardUserId, LeaderboardUserDTO> getMapEmpty()
    {
        return new HashMap<>();
    }

    protected List<LeaderboardUserDTO> getList1Item()
    {
        List<LeaderboardUserDTO> list = new ArrayList<>();
        list.add(getUser1());
        return list;
    }

    protected List<LeaderboardUserDTO> getList2Items()
    {
        List<LeaderboardUserDTO> list = new ArrayList<>();
        list.add(getUser1());
        list.add(getUser2());
        return list;
    }

    protected Map<LeaderboardUserId, LeaderboardUserDTO> getMap1Item()
    {
        Map<LeaderboardUserId, LeaderboardUserDTO> map = new HashMap<>();
        map.put(new LeaderboardUserId(11, 21l), getUser1());
        return map;
    }

    protected Map<LeaderboardUserId, LeaderboardUserDTO> getMap2Items()
    {
        Map<LeaderboardUserId, LeaderboardUserDTO> map = new HashMap<>();
        map.put(new LeaderboardUserId(11, 21l), getUser1());
        map.put(new LeaderboardUserId(12, 22l), getUser2());
        return map;
    }

    protected void assertEmpty(Map<LeaderboardUserId, LeaderboardUserDTO> map)
    {
        assertEquals(0, map.size());
    }

    protected void assertMap1Item(Map<LeaderboardUserId, LeaderboardUserDTO> map)
    {
        assertEquals(1, map.size());
        LeaderboardUserDTO value = map.get(new LeaderboardUserId(11, 21l));
        assertEquals(11, value.id);
        assertEquals(21, value.lbmuId);
    }

    protected void assertMap2Items(Map<LeaderboardUserId, LeaderboardUserDTO> map)
    {
        assertEquals(2, map.size());
        LeaderboardUserDTO value = map.get(new LeaderboardUserId(11, 21l));
        assertEquals(11, value.id);
        assertEquals(21, value.lbmuId);
        value = map.get(new LeaderboardUserId(12, 22l));
        assertEquals(12, value.id);
        assertEquals(22, value.lbmuId);
    }
}
