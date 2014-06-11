package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.api.leaderboard.SectorLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKeyFactory;
import java.util.HashMap;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefDTOFactory
{
    @NotNull private final Context context;
    @NotNull private final LeaderboardDefListKeyFactory leaderboardDefListKeyFactory;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOFactory(
            @NotNull Context context,
            @NotNull LeaderboardDefListKeyFactory leaderboardDefListKeyFactory)
    {
        this.context = context;
        this.leaderboardDefListKeyFactory = leaderboardDefListKeyFactory;
    }
    //</editor-fold>

    public void complementServerLeaderboardDefDTOs(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        leaderboardDefDTOs.add(new HeroLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new FollowerLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new FriendLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new ExchangeLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new SectorLeaderboardDefDTO(context));
    }

    @NotNull
    public HashMap<LeaderboardDefListKey, LeaderboardDefKeyList> file(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        HashMap<LeaderboardDefListKey, LeaderboardDefKeyList> filed = new HashMap<>();
        LeaderboardDefKeyList
                allKeys = new LeaderboardDefKeyList(),
                connectedKeys = new LeaderboardDefKeyList(),
                drillDownKeys = new LeaderboardDefKeyList(),
                sectorKeys = new LeaderboardDefKeyList(),
                exchangeKeys = new LeaderboardDefKeyList(),
                timePeriodKeys = new LeaderboardDefKeyList(),
                mostSkilledKeys = new LeaderboardDefKeyList();

        for (@NotNull LeaderboardDefDTO leaderboardDefDTO: leaderboardDefDTOs)
        {
            LeaderboardDefKey key = leaderboardDefDTO.getLeaderboardDefKey();

            allKeys.add(key);
            if (leaderboardDefDTO.exchangeRestrictions)
            {
                exchangeKeys.add(key);
            }
            else if (leaderboardDefDTO.sectorRestrictions)
            {
                sectorKeys.add(key);
            }
            else if (leaderboardDefDTO.isTimeRestrictedLeaderboard())
            {
                timePeriodKeys.add(key);
            }
            else if (leaderboardDefDTO.id == LeaderboardDefKeyKnowledge.MOST_SKILLED_ID)
            {
                mostSkilledKeys.add(key);
            }
            else if (leaderboardDefDTO instanceof DrillDownLeaderboardDefDTO)
            {
                drillDownKeys.add(key);
            }
            else if (leaderboardDefDTO instanceof ConnectedLeaderboardDefDTO)
            {
                connectedKeys.add(key);
            }
        }

        filed.put(leaderboardDefListKeyFactory.createMostSkilled(), mostSkilledKeys);
        filed.put(leaderboardDefListKeyFactory.createExchange(), exchangeKeys);
        filed.put(leaderboardDefListKeyFactory.createSector(), sectorKeys);
        filed.put(leaderboardDefListKeyFactory.createTimePeriod(), timePeriodKeys);
        filed.put(leaderboardDefListKeyFactory.createConnected(), connectedKeys);
        filed.put(leaderboardDefListKeyFactory.createDrillDown(), drillDownKeys);
        filed.put(new LeaderboardDefListKey(), allKeys);

        return filed;
    }
}
