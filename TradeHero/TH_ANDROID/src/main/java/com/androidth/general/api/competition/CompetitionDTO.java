package com.androidth.general.api.competition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.competition.key.CompetitionId;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.def.LeaderboardDefDTO;

public class CompetitionDTO implements DTO
{
    public int id;
    @Nullable public LeaderboardDefDTO leaderboard;
    public String name;
    public String competitionDurationType;
    public String iconActiveUrl;
    public String iconInactiveUrl;
    public String prizeValueWithCcy;
    @Nullable public LeaderboardUserDTO leaderboardUser;

    //<editor-fold desc="Constructors">
    public CompetitionDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull public CompetitionId getCompetitionId()
    {
        return new CompetitionId(id);
    }

    @Nullable public String getIconUrl()
    {
        LeaderboardDefDTO leaderboardCopy = this.leaderboard;
        if (leaderboardCopy != null)
        {
            return leaderboardCopy.isWithinUtcRestricted() ? iconActiveUrl : iconInactiveUrl;
        }
        return null;
    }
}
