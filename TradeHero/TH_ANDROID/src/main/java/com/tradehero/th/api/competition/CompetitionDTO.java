package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;

public class CompetitionDTO implements DTO
{
    public int id;
    public LeaderboardDefDTO leaderboard;
    public String name;
    public String competitionDurationType;
    public String iconActiveUrl;
    public String iconInactiveUrl;
    public String prizeValueWithCcy;

    //<editor-fold desc="Constructors">
    public CompetitionDTO()
    {
        super();
    }

    public CompetitionDTO(int id, LeaderboardDefDTO leaderboard, String name, String competitionDurationType, String iconActiveUrl,
            String iconInactiveUrl, String prizeValueWithCcy)
    {
        this.id = id;
        this.leaderboard = leaderboard;
        this.name = name;
        this.competitionDurationType = competitionDurationType;
        this.iconActiveUrl = iconActiveUrl;
        this.iconInactiveUrl = iconInactiveUrl;
        this.prizeValueWithCcy = prizeValueWithCcy;
    }
    //</editor-fold>

    public CompetitionId getCompetitionId()
    {
        return new CompetitionId(id);
    }

    public String getIconUrl()
    {
        LeaderboardDefDTO leaderboardCopy = this.leaderboard;
        if (leaderboardCopy != null)
        {
            Boolean isWithinUtcRestricted = leaderboardCopy.isWithinUtcRestricted();
            if (isWithinUtcRestricted != null && isWithinUtcRestricted)
            {
                return iconActiveUrl;
            }
            else if (isWithinUtcRestricted != null)
            {
                return iconInactiveUrl;
            }
        }
        return null;
    }
}
