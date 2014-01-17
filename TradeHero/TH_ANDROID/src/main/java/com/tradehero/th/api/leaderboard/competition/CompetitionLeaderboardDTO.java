package com.tradehero.th.api.leaderboard.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:39 PM To change this template use File | Settings | File Templates. */
public class CompetitionLeaderboardDTO implements DTO
{
    public static final String TAG = CompetitionLeaderboardDTO.class.getSimpleName();

    public LeaderboardDTO leaderboard;
    public List<AdDTO> ads;
    public int adFrequencyRows;
    public int adStartRow;
    public List<PrizeDTO> prizes;
}
