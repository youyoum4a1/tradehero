package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;

public class CompetitionDTO implements DTO
{
    public int id;
    @Nullable public LeaderboardDefDTO leaderboard;
    public String name;
    public String competitionDurationType;
    public String iconActiveUrl;
    public String iconInactiveUrl;
    public String prizeValueWithCcy;
    @Nullable public StocksLeaderboardUserDTO leaderboardUser;

    //<editor-fold desc="Constructors">
    public CompetitionDTO()
    {
        super();
    }

    public CompetitionDTO(int id, @Nullable LeaderboardDefDTO leaderboard, String name, String competitionDurationType, String iconActiveUrl,
            String iconInactiveUrl, String prizeValueWithCcy, StocksLeaderboardUserDTO leaderboardUser)
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
