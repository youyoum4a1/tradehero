package com.ayondo.academy.api.competition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.competition.key.CompetitionId;
import com.ayondo.academy.api.leaderboard.LeaderboardUserDTO;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;

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
