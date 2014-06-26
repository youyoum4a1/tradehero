package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;

// The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
class LeaderboardCutDTO implements DTO
{
    public final int id;
    public final String name;
    public final List<LeaderboardUserId> userIds;
    public final int userIsAtPositionZeroBased;
    public final Date markUtc;
    public final int minPositionCount;
    public final double maxSharpeRatioInPeriodVsSP500;
    public final double maxStdDevPositionRoiInPeriod;
    public final double avgStdDevPositionRoiInPeriod;
    @NotNull public final Date expirationDate;

    public LeaderboardCutDTO(
            @NotNull LeaderboardDTO leaderboardDTO,
            @NotNull LeaderboardUserCache leaderboardUserCache,
            @NotNull LeaderboardUserDTOUtil leaderboardUserDTOUtil)
    {
        this.id = leaderboardDTO.id;
        this.name = leaderboardDTO.name;

        leaderboardUserCache.put(leaderboardUserDTOUtil.map(leaderboardDTO.users));
        userIds = leaderboardUserDTOUtil.getIds(leaderboardDTO.users);

        this.userIsAtPositionZeroBased = leaderboardDTO.userIsAtPositionZeroBased;
        this.markUtc = leaderboardDTO.markUtc;
        this.minPositionCount = leaderboardDTO.minPositionCount;
        this.maxSharpeRatioInPeriodVsSP500 = leaderboardDTO.maxSharpeRatioInPeriodVsSP500;
        this.maxStdDevPositionRoiInPeriod = leaderboardDTO.maxStdDevPositionRoiInPeriod;
        this.avgStdDevPositionRoiInPeriod = leaderboardDTO.avgStdDevPositionRoiInPeriod;
        this.expirationDate = leaderboardDTO.expirationDate;
    }

    @NotNull public LeaderboardDTO create(@NotNull LeaderboardUserCache leaderboardUserCache)
    {
        return new LeaderboardDTO(
                id,
                name,
                leaderboardUserCache.get(userIds),
                userIsAtPositionZeroBased,
                markUtc,
                minPositionCount,
                maxSharpeRatioInPeriodVsSP500,
                maxStdDevPositionRoiInPeriod,
                avgStdDevPositionRoiInPeriod,
                expirationDate);
    }
}
