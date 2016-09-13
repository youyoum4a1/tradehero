package com.androidth.general.api.leaderboard;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.androidth.general.common.persistence.BaseHasExpiration;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import java.util.Date;

public class LeaderboardDTO extends BaseHasExpiration
        implements DTO, ContainerDTO<LeaderboardUserDTO, LeaderboardUserDTOList>
{
    public static final String INCLUDE_FOF = "INCLUDE_FOF";
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 300;

    public int id;
    public String name;
    public LeaderboardUserDTOList users;
    public LeaderboardUserDTOList neighbours;
    public int userIsAtPositionZeroBased;
    public Date markUtc;

    public int minPositionCount;
    @JsonProperty("max_sharpeRatioInPeriod_vsSP500")
    public double maxSharpeRatioInPeriodVsSP500;
    @JsonProperty("max_stddev_positionRoiInPeriod")
    public double maxStdDevPositionRoiInPeriod;
    @JsonProperty("avg_stddev_positionRoiInPeriod")
    public double avgStdDevPositionRoiInPeriod;

    public Integer capAt;

    //<editor-fold desc="Constructors">
    public LeaderboardDTO()
    {
        super(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }
    //</editor-fold>

    @JsonIgnore
    @NonNull public LeaderboardKey getLeaderboardKey()
    {
        return new LeaderboardKey(id);
    }

    @JsonIgnore
    public double getAvgVolatility()
    {
        return avgStdDevPositionRoiInPeriod;
    }

    @JsonIgnore
    public double getAvgConsistency()
    {
        Double v = getAvgVolatility();
        if (v != null && v != 0)
        {
            return 1/v;
        }
        return (double)2;
    }

    @Override public int size()
    {
        return users == null ? 0 : users.size();
    }

    @Override @NonNull public LeaderboardUserDTOList getList()
    {
        return users == null ? new LeaderboardUserDTOList() : users;
    }
}