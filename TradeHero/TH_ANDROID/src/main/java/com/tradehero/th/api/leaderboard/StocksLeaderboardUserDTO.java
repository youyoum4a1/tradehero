package com.tradehero.th.api.leaderboard;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StocksLeaderboardUserDTO extends BaseLeaderboardUserDTO
{
    public static final Double MIN_CONSISTENCY = 0.004;

    public double PLinPeriodRefCcy;
    public double investedAmountRefCcy;

    public int numberOfTradesInPeriod;
    public int numberOfPositionsInPeriod;
    @Nullable public Double avgNumberOfTradesPerMonth;

    public int avgHoldingPeriodMins;

    // additional fields for most skilled
    @JsonProperty("stddev_positionRoiInPeriod")
    @Nullable public Double stdDevPositionRoiInPeriod;
    @JsonProperty("sharpeRatioInPeriod_vsSP500")
    public Double sharpeRatioInPeriodVsSP500;
    public Double benchmarkRoiInPeriod;
    @JsonProperty("avg_positionRoiInPeriod")
    public Double avgPositionRoiInPeriod;
    public Double winRatio;
    public Double starRating;
    public Double heroQuotient;
    public Double ordinalPositionNormalized;
    public Integer followerCountFree;
    public Integer followerCountPaid;
    public Integer commentCount;

    public StocksLeaderboardUserDTO()
    {
        super();
    }

    public int getCommentsCount()
    {
        return commentCount == null ? 0 : commentCount;
    }

    public int getTotalFollowersCount()
    {
        return (followerCountFree != null ? followerCountFree : 0) + (followerCountPaid != null ? followerCountPaid : 0);
    }

    public double getWinRatio()
    {
        return winRatio != null ? winRatio : 0;
    }

    public int getNumberOfTrades()
    {
        return numberOfTradesInPeriod;
    }

    public double getBenchmarkRoiInPeriod()
    {
        return benchmarkRoiInPeriod != null ? benchmarkRoiInPeriod : 0;
    }

    //<editor-fold desc="ExtendedDTO">
    @JsonIgnore
    public Double getConsistency()
    {
        if (stdDevPositionRoiInPeriod != null && stdDevPositionRoiInPeriod != 0)
        {
            return 1 / stdDevPositionRoiInPeriod;
        }
        return null;
    }

    //</editor-fold>
}

