package com.ayondo.academy.api.leaderboard.competition;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.competition.AdDTO;
import com.ayondo.academy.api.competition.PrizeDTO;
import com.ayondo.academy.api.leaderboard.LeaderboardDTO;
import java.util.List;

public class CompetitionLeaderboardDTO implements DTO
{
    @Nullable public LeaderboardDTO leaderboard;
    public List<AdDTO> ads;
    public int adFrequencyRows;
    public int adStartRow;
    public List<PrizeDTO> prizes;

    @JsonIgnore public int getPrizeSize()
    {
        return prizes == null ? 0 : prizes.size();
    }
}
