package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.api.leaderboard.SectorContainerLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.ExchangeLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.SectorLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.TimePeriodLeaderboardDefListKey;
import java.util.HashMap;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefDTOFactory
{
    @NotNull private final Context context;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOFactory(
            @NotNull Context context)
    {
        this.context = context;
    }
    //</editor-fold>

    public void complementServerLeaderboardDefDTOs(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        leaderboardDefDTOs.add(new HeroLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new FollowerLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new FriendLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new ExchangeContainerLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new SectorContainerLeaderboardDefDTO(context));
    }

    @NotNull
    public HashMap<LeaderboardDefListKey, LeaderboardDefKeyList> file(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        HashMap<LeaderboardDefListKey, LeaderboardDefKeyList> filed = new HashMap<>();
        filed.put(new LeaderboardDefListKey(), new LeaderboardDefKeyList());
        filed.put(new ConnectedLeaderboardDefListKey(), new LeaderboardDefKeyList());
        filed.put(new DrillDownLeaderboardDefListKey(), new LeaderboardDefKeyList());
        filed.put(new SectorLeaderboardDefListKey(), new LeaderboardDefKeyList());
        filed.put(new ExchangeLeaderboardDefListKey(), new LeaderboardDefKeyList());
        filed.put(new TimePeriodLeaderboardDefListKey(), new LeaderboardDefKeyList());
        filed.put(new MostSkilledLeaderboardDefListKey(), new LeaderboardDefKeyList());

        for (@NotNull LeaderboardDefDTO leaderboardDefDTO: leaderboardDefDTOs)
        {
            LeaderboardDefKey key = leaderboardDefDTO.getLeaderboardDefKey();

            filed.get(new LeaderboardDefListKey()).add(key);
            LeaderboardDefListKey listKey = leaderboardDefDTO.getLeaderboardDefListKey();
            filed.get(listKey).add(key);
        }
        return filed;
    }
}
