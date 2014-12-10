package com.tradehero.th.api.leaderboard;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.models.number.THSignedNumber;
import java.util.Date;
import java.util.List;

//TODO Just a placeholder class until API format is finalized.
public class FxLeaderboardUserDTO extends BaseLeaderboardUserDTO
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

    public FxLeaderboardUserDTO()
    {
        super();
    }

    public String getHeroQuotientFormatted()
    {
        if (starRating == null)
        {
            return "0";
        }

        return THSignedNumber.builder(starRating).withOutSign().build().toString();
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

    @Nullable public Double getVolatility()
    {
        return stdDevPositionRoiInPeriod;
    }

    public int getNumberOfTrades()
    {
        return numberOfTradesInPeriod;
    }

    public double getBenchmarkRoiInPeriod()
    {
        return benchmarkRoiInPeriod != null ? benchmarkRoiInPeriod : 0;
    }

    public double normalizePerformance()
    {
        try
        {
            if (sharpeRatioInPeriodVsSP500 == null)
            {
                return 0;
            }
            Double v = sharpeRatioInPeriodVsSP500;
            Double min = (double) -2;
            Double max = (double) 2;

            if (v > max)
            {
                v = max;
            }
            else if (v < min)
            {
                v = min;
            }
            double r = 100 * (v - min) / (max - min);
            //Timber.d("normalizePerformance sharpeRatioInPeriodVsSP500 %s result %s",
            //        sharpeRatioInPeriodVsSP500, r);

            return r;
        } catch (Exception e)
        {
            //Timber.e("normalizePerformance", e);
        }
        return 0;
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

