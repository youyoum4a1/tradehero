package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.api.leaderboard.SectorLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import java.util.HashMap;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefDTOFactory
{
    @NotNull private final Context context;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOFactory(@NotNull Context context)
    {
        this.context = context;
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

        filed.put(LeaderboardDefListKey.getMostSkilled(), mostSkilledKeys);
        filed.put(LeaderboardDefListKey.getExchange(), exchangeKeys);
        filed.put(LeaderboardDefListKey.getSector(), sectorKeys);
        filed.put(LeaderboardDefListKey.getTimePeriod(), timePeriodKeys);
        filed.put(LeaderboardDefListKey.getConnected(), connectedKeys);
        filed.put(LeaderboardDefListKey.getDrillDown(), drillDownKeys);
        filed.put(new LeaderboardDefListKey(), allKeys);

        return filed;
    }
}
