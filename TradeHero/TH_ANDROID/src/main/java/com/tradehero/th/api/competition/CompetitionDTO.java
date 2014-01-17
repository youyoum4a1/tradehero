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

    public CompetitionId getCompetitionId()
    {
        return new CompetitionId(id);
    }
}
