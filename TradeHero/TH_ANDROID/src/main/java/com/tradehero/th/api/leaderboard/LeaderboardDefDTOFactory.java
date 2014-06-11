package com.tradehero.th.api.leaderboard;

import android.content.Context;
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
}
