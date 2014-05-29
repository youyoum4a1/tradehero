package com.tradehero.th.api.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.SecurityUtils;
import java.util.Date;
import java.util.List;

public class LeaderboardUserDTO extends UserBaseDTO
    implements ExpandableItem
{
    public static final String LEADERBOARD_PERIOD_START_STRING = "LEADERBOARD_PERIOD_START_STRING";
    private static final String LEADERBOARD_USER_POSITION = "LEADERBOARD_USER_POSITION";
    private static final String LEADERBOARD_ID = "LEADERBOARD_ID";
    private static final String LEADERBOARD_INCLUDE_FOF = "LEADERBOARD_INCLUDE_FOF";

    public long lbmuId;    // leaderboardMarkUser.id ...
    public int portfolioId;    // ...OR portfolioId --> messy

    @JsonProperty("friendOf_UserIds")
    public List<Integer> friendOfUserIds;    // client expects userIds here to be present in LeaderboardDTO.users collection!
    @JsonProperty("friendOf_markupString")
    public String friendOfMarkupString;

    public double roiInPeriod;
    public double PLinPeriodRefCcy;
    public double roiAnnualizedInPeriod;
    public double investedAmountRefCcy;

    public int numberOfTradesInPeriod;
    public int numberOfPositionsInPeriod;
    public Double avgNumberOfTradesPerMonth;

    public int ordinalPosition; // OK

    public int avgHoldingPeriodMins;

    // additional fields for most skilled
    public Date periodStartUtc;
    public Date periodEndUtc;
    @JsonProperty("stddev_positionRoiInPeriod")
    public Double stdDevPositionRoiInPeriod;
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
    public String currencyDisplay;
    public String currencyISO;

    public String name;
    public String thUserId;
    public String fbId;
    public String fbPicUrl;
    public boolean alreadyInvited;

    public LeaderboardUserDTO()
    {
        super();
    }

    public LeaderboardUserId getLeaderboardUserId()
    {
        return new LeaderboardUserId(id, lbmuId);
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

    @JsonIgnore
    public String getNiceCurrency()
    {
        if (hasValidCurrencyDisplay())
        {
            return currencyDisplay;
        }
        return SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    @JsonIgnore
    public boolean hasValidCurrencyDisplay()
    {
        return currencyDisplay != null && !currencyDisplay.isEmpty();
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

