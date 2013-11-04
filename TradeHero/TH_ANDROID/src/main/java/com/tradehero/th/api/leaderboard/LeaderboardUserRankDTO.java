package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.loaders.ItemWithComparableId;
import com.tradehero.th.utils.NumberDisplayUtils;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:09 PM Copyright (c) TradeHero */

public class LeaderboardUserRankDTO extends UserBaseDTO
        implements ItemWithComparableId<Integer>
{
    public int lbmuId;    // leaderboardMarkUser.id ...
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
    private Double sharpeRatioInPeriod_vsSP500;
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
        return lbmuId;
    }

    @Override public void setId(Integer id)
    {
        lbmuId = id;
    }

    @Override public int compareTo(ItemWithComparableId<Integer> other)
    {
        return other.getId().compareTo(lbmuId);
    }

    public String getHeroQuotientFormatted()
    {
        if (starRating == null)
        {
            return "0";
        }

        return NumberDisplayUtils.formatWithRelevantDigits(starRating, 0);
    }

    public double getSharpeRatioInPeriod()
    {
        return sharpeRatioInPeriod_vsSP500 == null ? 0 : sharpeRatioInPeriod_vsSP500;
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

    public int getPositions()
    {
        // TODO calculation
        return 0;
    }

    public String getFormattedPL()
    {
        DecimalFormat df = new DecimalFormat("000,000.00");
        return df.format(PLinPeriodRefCcy);
    }
}

