package com.tradehero.th.api.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.BaseHasExpiration;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.base.Application;
import com.tradehero.th.utils.DateUtils;
import java.io.IOException;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDTO extends BaseHasExpiration
        implements DTO
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




    //<editor-fold desc="Constructors">
    public LeaderboardDTO()
    {
        super(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public LeaderboardDTO(
            int id,
            String name,
            LeaderboardUserDTOList users,
            LeaderboardUserDTOList neighbours,
            int userIsAtPositionZeroBased,
            Date markUtc,
            int minPositionCount,
            double maxSharpeRatioInPeriodVsSP500,
            double maxStdDevPositionRoiInPeriod,
            double avgStdDevPositionRoiInPeriod,
            @NotNull Date expirationDate)
    {
        super(expirationDate);
        this.id = id;
        this.name = name;
        this.users = users;
        this.neighbours = neighbours;
        this.userIsAtPositionZeroBased = userIsAtPositionZeroBased;
        this.markUtc = markUtc;
        this.minPositionCount = minPositionCount;
        this.maxSharpeRatioInPeriodVsSP500 = maxSharpeRatioInPeriodVsSP500;
        this.maxStdDevPositionRoiInPeriod = maxStdDevPositionRoiInPeriod;
        this.avgStdDevPositionRoiInPeriod = avgStdDevPositionRoiInPeriod;
    }
    //</editor-fold>

    @JsonIgnore
    @NotNull public LeaderboardKey getLeaderboardKey()
    {
        return new LeaderboardKey(id);
    }

    @Override
    public String toString()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Failed to json";
        }
    }

    @JsonIgnore
    public Double getAvgVolatility()
    {
        return avgStdDevPositionRoiInPeriod;
    }

    @JsonIgnore
    public Double getAvgConsistency()
    {
        Double v = getAvgVolatility();
        if (v != null && v != 0)
        {
            return 1/v;
        }
        return (double)2;
    }

    public String getMarkUTCString()
    {
        return DateUtils.getFormattedUtcDate(Application.context().getResources(), markUtc);
    }
}