package com.tradehero.th.api.leaderboard.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import java.util.List;
import android.support.annotation.Nullable;

public class CompetitionLeaderboardDTO implements DTO
{
    @Nullable public LeaderboardDTO leaderboard;
    public List<AdDTO> ads;
    public int adFrequencyRows;
    public int adStartRow;
    public List<PrizeDTO> prizes;

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    public PrizeDTO getPrizeAt(int position)
    {
        if (prizes != null && position >= 0 && position < prizes.size())
        {
            return prizes.get(position);
        }
        return null;
    }
}
