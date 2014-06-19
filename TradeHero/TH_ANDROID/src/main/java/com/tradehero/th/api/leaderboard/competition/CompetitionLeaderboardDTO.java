package com.tradehero.th.api.leaderboard.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class CompetitionLeaderboardDTO implements DTO
{
    @Nullable public LeaderboardDTO leaderboard;
    public List<AdDTO> ads;
    public int adFrequencyRows;
    public int adStartRow;
    public List<PrizeDTO> prizes;

    public CompetitionLeaderboardDTO()
    {
    }

    public CompetitionLeaderboardDTO(@Nullable LeaderboardDTO leaderboard, List<AdDTO> ads, int adFrequencyRows, int adStartRow,
            List<PrizeDTO> prizes)
    {
        this.leaderboard = leaderboard;
        this.ads = ads;
        this.adFrequencyRows = adFrequencyRows;
        this.adStartRow = adStartRow;
        this.prizes = prizes;
    }
}
