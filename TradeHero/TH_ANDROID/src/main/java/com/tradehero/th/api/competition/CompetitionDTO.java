package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import android.support.annotation.Nullable;

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

    public CompetitionDTO(int id, @Nullable LeaderboardDefDTO leaderboard, String name, String competitionDurationType, String iconActiveUrl,
            String iconInactiveUrl, String prizeValueWithCcy, LeaderboardUserDTO leaderboardUser)
    {
        this.id = id;
        this.leaderboard = leaderboard;
        this.name = name;
        this.competitionDurationType = competitionDurationType;
        this.iconActiveUrl = iconActiveUrl;
        this.iconInactiveUrl = iconInactiveUrl;
        this.prizeValueWithCcy = prizeValueWithCcy;
        this.leaderboardUser = leaderboardUser;
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
