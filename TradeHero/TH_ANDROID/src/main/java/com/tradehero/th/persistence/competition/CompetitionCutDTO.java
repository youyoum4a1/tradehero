package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserCache;
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
    @Nullable public final LeaderboardUserId leaderboardUserId;

    public CompetitionCutDTO(
            @NotNull CompetitionDTO competitionDTO,
            @NotNull LeaderboardDefCache leaderboardDefCache,
            @NotNull LeaderboardUserCache leaderboardUserCache)
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
        if (competitionDTO.leaderboardUser != null)
        {
            LeaderboardUserId key = competitionDTO.leaderboardUser.getLeaderboardUserId();
            leaderboardUserCache.put(key, competitionDTO.leaderboardUser);
            this.leaderboardUserId = key;
        }
        else
        {
            this.leaderboardUserId = null;
        }
    }

    @Nullable public CompetitionDTO create(
            @NotNull LeaderboardDefCache leaderboardDefCache,
            @NotNull LeaderboardUserCache leaderboardUserCache)
    {
        LeaderboardDefDTO cachedLeaderboardDef = null;
        if (leaderboardKey != null)
        {
            cachedLeaderboardDef = leaderboardDefCache.get(leaderboardKey);
            if (cachedLeaderboardDef == null)
            {
                return null;
            }
        }
        LeaderboardUserDTO cachedLeaderboardUser = null;
        if (leaderboardUserId != null)
        {
            cachedLeaderboardUser = leaderboardUserCache.get(leaderboardUserId);
            if (cachedLeaderboardUser == null)
            {
                return null;
            }
        }
        return new CompetitionDTO(
                id,
                cachedLeaderboardDef,
                name,
                competitionDurationType,
                iconActiveUrl,
                iconInactiveUrl,
                prizeValueWithCcy,
                cachedLeaderboardUser
        );
    }
}
