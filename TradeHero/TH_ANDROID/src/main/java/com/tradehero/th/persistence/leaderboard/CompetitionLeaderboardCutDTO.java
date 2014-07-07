package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
class CompetitionLeaderboardCutDTO implements DTO
{
    @Nullable public final LeaderboardKey leaderboardKey;
    public final List<AdDTO> ads;
    public final int adFrequencyRows;
    public final int adStartRow;
    public final List<PrizeDTO> prizes;

    public CompetitionLeaderboardCutDTO(
            @NotNull CompetitionLeaderboardDTO competitionLeaderboardDTO,
            @NotNull LeaderboardCache leaderboardCache)
    {
        if (competitionLeaderboardDTO.leaderboard != null)
        {
            leaderboardCache.put(competitionLeaderboardDTO.leaderboard.getLeaderboardKey(), competitionLeaderboardDTO.leaderboard);
            this.leaderboardKey = competitionLeaderboardDTO.leaderboard.getLeaderboardKey();
        }
        else
        {
            this.leaderboardKey = null;
        }

        ads = competitionLeaderboardDTO.ads;
        adFrequencyRows = competitionLeaderboardDTO.adFrequencyRows;
        adStartRow = competitionLeaderboardDTO.adStartRow;
        prizes = competitionLeaderboardDTO.prizes;
    }

    public CompetitionLeaderboardDTO create(@NotNull LeaderboardCache leaderboardCache)
    {
        LeaderboardDTO leaderboardDTO = null;
        if (leaderboardKey != null)
        {
            leaderboardDTO = leaderboardCache.get(leaderboardKey);
            if (leaderboardDTO == null)
            {
                return null;
            }
        }
        return new CompetitionLeaderboardDTO(
                leaderboardDTO,
                ads,
                adFrequencyRows,
                adStartRow,
                prizes
        );
    }
}
