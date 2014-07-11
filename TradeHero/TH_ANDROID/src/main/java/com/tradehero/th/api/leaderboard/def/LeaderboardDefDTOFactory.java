package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.api.leaderboard.SectorContainerLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.ExchangeLeaderboardDefListKey;
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
    public HashMap<LeaderboardDefListKey, LeaderboardDefDTOList> file(@NotNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        HashMap<LeaderboardDefListKey, LeaderboardDefDTOList> filed = new HashMap<>();
        filed.put(new LeaderboardDefListKey(), new LeaderboardDefDTOList());
        filed.put(new ConnectedLeaderboardDefListKey(), new LeaderboardDefDTOList());
        filed.put(new DrillDownLeaderboardDefListKey(), new LeaderboardDefDTOList());
        filed.put(new SectorLeaderboardDefListKey(), new LeaderboardDefDTOList());
        filed.put(new ExchangeLeaderboardDefListKey(), new LeaderboardDefDTOList());
        filed.put(new TimePeriodLeaderboardDefListKey(), new LeaderboardDefDTOList());
        filed.put(new MostSkilledLeaderboardDefListKey(), new LeaderboardDefDTOList());

        for (@NotNull LeaderboardDefDTO leaderboardDefDTO: leaderboardDefDTOs)
        {
            filed.get(new LeaderboardDefListKey()).add(leaderboardDefDTO);
            LeaderboardDefListKey listKey = leaderboardDefDTO.getLeaderboardDefListKey();
            filed.get(listKey).add(leaderboardDefDTO);
        }
        return filed;
    }
}
