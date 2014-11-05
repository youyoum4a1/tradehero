package com.tradehero.th.persistence.leaderboard;

import com.tradehero.common.persistence.BaseHasExpiration;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOUtil;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserIdList;
import java.util.Date;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// The purpose of this class is to save on memory usage by cutting out the elements that already enjoy their own cache.
class LeaderboardCutDTO extends BaseHasExpiration
        implements DTO
{
    public final int id;
    public final String name;
    public final LeaderboardUserIdList userIds;
    public LeaderboardUserDTOList neighbours;
    public final int userIsAtPositionZeroBased;
    public final Date markUtc;
    public final int minPositionCount;
    public final double maxSharpeRatioInPeriodVsSP500;
    public final double maxStdDevPositionRoiInPeriod;
    public final double avgStdDevPositionRoiInPeriod;

    public LeaderboardCutDTO(
            @NonNull LeaderboardDTO leaderboardDTO,
            @NonNull LeaderboardUserCache leaderboardUserCache,
            @NonNull LeaderboardUserDTOUtil leaderboardUserDTOUtil)
    {
        super(leaderboardDTO.expirationDate);
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
    }

    @Nullable public LeaderboardDTO create(@NonNull LeaderboardUserCache leaderboardUserCache)
    {
        LeaderboardUserDTOList leaderboardUserDTOs = leaderboardUserCache.get(userIds);
        if (leaderboardUserDTOs != null && leaderboardUserDTOs.hasNullItem())
        {
            return null;
        }
        return new LeaderboardDTO(
                id,
                name,
                leaderboardUserDTOs,
                neighbours,
                userIsAtPositionZeroBased,
                markUtc,
                minPositionCount,
                maxSharpeRatioInPeriodVsSP500,
                maxStdDevPositionRoiInPeriod,
                avgStdDevPositionRoiInPeriod,
                expirationDate);
    }
}
