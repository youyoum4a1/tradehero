package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.loaders.ItemWithComparableId;
import com.tradehero.th.utils.NumberDisplayUtils;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:09 PM Copyright (c) TradeHero */

public class LeaderboardUserRankDTO extends UserBaseDTO
        implements ItemWithComparableId<Integer>
{
    public long lbmuId;    // leaderboardMarkUser.id ...
    public int portfolioId;    // ...OR portfolioId --> messy

    public List<Integer> friendOf_UserIds;    // client expects userIds here to be present in LeaderboardDTO.users collection!
    public String friendOf_markupString;

    public double roiInPeriod;
    public double PLinPeriodRefCcy;
    public double roiAnnualizedInPeriod;
    public double investedAmountRefCcy;

    public int numberOfTradesInPeriod;
    public int numberOfPositionsInPeriod;

    public int ordinalPosition; // OK

    public int avgHoldingPeriodMins;

    // additional fields for most skilled
    public Date periodStartUtc;
    public Date periodEndUtc;
    public Double stddev_positionRoiInPeriod;
    public Double sharpeRatioInPeriod_vsSP500;
    public Double benchmarkRoiInPeriod;
    public Double avg_positionRoiInPeriod;
    public Double winRatio;
    public Double starRating;
    public Double heroQuotient;
    public Double ordinalPositionNormalized;
    public Integer followerCountFree;
    public Integer followerCountPaid;
    private Integer commentCount;

    public LeaderboardUserRankDTO()
    {
        super();
    }

    @Override public Integer getId()
    {
        return id;
    }

    @Override public void setId(Integer id)
    {
        this.id = id;
    }

    @Override public int compareTo(ItemWithComparableId<Integer> other)
    {
        return other.getId().compareTo(id);
    }

    public String getHeroQuotientFormatted()
    {
        if (starRating == null)
        {
            return "0";
        }

        return NumberDisplayUtils.formatWithRelevantDigits(starRating, 0);
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

    public String getFormattedPL()
    {
        DecimalFormat df = new DecimalFormat("###,##0.00");
        return df.format(PLinPeriodRefCcy);
    }

    public String getFormattedSharpeRatio()
    {
        if (sharpeRatioInPeriod_vsSP500 != null)
        {
            DecimalFormat df = new DecimalFormat("###,##0.0000");
            return df.format(sharpeRatioInPeriod_vsSP500);
        }
        return "0";
    }

    public Double getVolatility()
    {
        return stddev_positionRoiInPeriod;
    }

    public int getNumberOfTrades()
    {
        return numberOfTradesInPeriod;
    }

    public double getBenchmarkRoiInPeriod()
    {
        return benchmarkRoiInPeriod != null ? benchmarkRoiInPeriod : 0;
    }
}

