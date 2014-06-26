package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
class CompetitionCutDTO implements DTO
{
    public final int id;
    @Nullable public final LeaderboardDefKey leaderboardKey;
    public final String name;
    public final String competitionDurationType;
    public final String iconActiveUrl;
    public final String iconInactiveUrl;
    public final String prizeValueWithCcy;

    public CompetitionCutDTO(
            @NotNull CompetitionDTO competitionDTO,
            @NotNull LeaderboardDefCache leaderboardDefCache)
    {
        this.id = competitionDTO.id;

        if (competitionDTO.leaderboard == null)
        {
            leaderboardKey = null;
        }
        else
        {
            LeaderboardDefKey key = competitionDTO.leaderboard.getLeaderboardDefKey();
            leaderboardDefCache.put(key, competitionDTO.leaderboard);
            leaderboardKey = key;
        }

        this.name = competitionDTO.name;
        this.competitionDurationType = competitionDTO.competitionDurationType;
        this.iconActiveUrl = competitionDTO.iconActiveUrl;
        this.iconInactiveUrl = competitionDTO.iconInactiveUrl;
        this.prizeValueWithCcy = competitionDTO.prizeValueWithCcy;
    }

    @NotNull public CompetitionDTO create(@NotNull LeaderboardDefCache leaderboardDefCache)
    {
        return new CompetitionDTO(
                id,
                leaderboardKey == null ? null : leaderboardDefCache.get(leaderboardKey),
                name,
                competitionDurationType,
                iconActiveUrl,
                iconInactiveUrl,
                prizeValueWithCcy
        );
    }
}
