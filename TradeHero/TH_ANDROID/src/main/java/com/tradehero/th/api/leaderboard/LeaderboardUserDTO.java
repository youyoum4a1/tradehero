package com.tradehero.th.api.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.THSignedNumber;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:09 PM Copyright (c) TradeHero */

public class LeaderboardUserDTO extends UserBaseDTO
    implements ExpandableItem
{
    public static final String LEADERBOARD_PERIOD_START_STRING = "LEADERBOARD_PERIOD_START_STRING";
    private static final String LEADERBOARD_USER_POSITION = "LEADERBOARD_USER_POSITION";
    private static final String LEADERBOARD_ID = "LEADERBOARD_ID";
    private static final String LEADERBOARD_INCLUDE_FOF = "LEADERBOARD_INCLUDE_FOF";

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
    public int avgNumberOfTradesPerMonth;

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

    public LeaderboardUserDTO()
    {
        super();
    }

    public LeaderboardUserId getLeaderboardUserId()
    {
        return new LeaderboardUserId(lbmuId);
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

    //<editor-fold desc="ExtendedDTO">
    @JsonIgnore
    public void setPosition(Integer position)
    {
        this.put(LEADERBOARD_USER_POSITION, position);
    }

    @JsonIgnore
    public Integer getPosition()
    {
        return (Integer)get(LEADERBOARD_USER_POSITION);
    }

    @JsonIgnore
    public void setLeaderboardId(Integer leaderboardId)
    {
        this.put(LEADERBOARD_ID, leaderboardId);
    }

    @JsonIgnore
    public Integer getLeaderboardId()
    {
        return (Integer)get(LEADERBOARD_ID);
    }

    @JsonIgnore
    public Boolean isIncludeFoF()
    {
        return (Boolean)get(LEADERBOARD_INCLUDE_FOF);
    }

    @JsonIgnore
    public void setIncludeFoF(boolean includeFoF)
    {
        this.put(LEADERBOARD_INCLUDE_FOF, includeFoF);
    }

    @JsonIgnore
    @Override public boolean isExpanded()
    {
        return (Boolean)get(ExpandableItem.class.getName(), false);
    }

    @JsonIgnore
    @Override public void setExpanded(boolean expanded)
    {
        this.put(ExpandableItem.class.getName(), expanded);
    }
    //</editor-fold>
}

