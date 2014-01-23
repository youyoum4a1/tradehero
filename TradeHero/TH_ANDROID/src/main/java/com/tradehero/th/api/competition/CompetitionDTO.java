package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:36 PM To change this template use File | Settings | File Templates. */
public class CompetitionDTO implements DTO
{
    public static final String TAG = CompetitionDTO.class.getSimpleName();

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
