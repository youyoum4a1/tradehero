package com.tradehero.th.api.leaderboard;

import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.loaders.ItemWithComparableId;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:09 PM Copyright (c) TradeHero */

public class LeaderboardUserDTO extends UserBaseDTO implements ItemWithComparableId<Integer>
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
    public Double sharpeRatioInPeriod_vsSP500;
    public Double benchmarkRoiInPeriod;
    public Double avg_positionRoiInPeriod;
    public Double winRatio;
    public Double starRating;
    public Double heroQuotient;
    public Double ordinalPositionNormalized;
    public Integer followerCountFree;
    public Integer followerCountPaid;
    public Integer commentCount;

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
}

