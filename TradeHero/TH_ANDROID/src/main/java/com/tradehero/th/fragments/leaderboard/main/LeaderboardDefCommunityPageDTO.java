package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import org.jetbrains.annotations.NotNull;

class LeaderboardDefCommunityPageDTO implements CommunityPageDTO
{
    @NotNull public final LeaderboardDefDTO leaderboardDefDTO;

    //<editor-fold desc="Constructors">
    public LeaderboardDefCommunityPageDTO(@NotNull LeaderboardDefDTO leaderboardDefDTO)
    {
        this.leaderboardDefDTO = leaderboardDefDTO;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return leaderboardDefDTO.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof LeaderboardDefCommunityPageDTO))
        {
            return false;
        }
        LeaderboardDefDTO otherDTO = ((LeaderboardDefCommunityPageDTO) other).leaderboardDefDTO;
        if (this.leaderboardDefDTO == otherDTO)
        {
            return true;
        }
        return this.leaderboardDefDTO.getClass().equals(otherDTO.getClass()) &&
            this.leaderboardDefDTO.getLeaderboardDefKey().equals(otherDTO.getLeaderboardDefKey());
    }
}
