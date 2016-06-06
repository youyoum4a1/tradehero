package com.androidth.general.api.leaderboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.androidth.general.adapters.ExpandableItem;
import com.androidth.general.api.leaderboard.key.LeaderboardUserId;
import com.androidth.general.api.leaderboard.position.LeaderboardMarkUserId;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.position.GetPositionsDTOKey;
import com.androidth.general.api.social.FollowDetailsDTO;
import com.androidth.general.api.social.FollowStatesDTO;
import com.androidth.general.api.users.UserBaseDTO;
import com.androidth.general.utils.SecurityUtils;
import java.util.Date;
import java.util.List;

public class LeaderboardUserDTO extends UserBaseDTO
        implements ExpandableItem
{
    private static final String LEADERBOARD_USER_POSITION = "LEADERBOARD_USER_POSITION";
    private static final String LEADERBOARD_ID = "LEADERBOARD_ID";
    private static final String LEADERBOARD_INCLUDE_FOF = "LEADERBOARD_INCLUDE_FOF";
    public static final Double MIN_CONSISTENCY = 0.004;

    public long lbmuId;    // leaderboardMarkUser.id ..., will be null if user not ranked
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
    @Nullable public Double avgNumberOfTradesPerMonth;

    public int ordinalPosition; // OK

    public int avgHoldingPeriodMins;

    public Date periodStartUtc;
    public Date periodEndUtc;
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
    public String currencyDisplay;
    public String currencyISO;
    public FollowDetailsDTO relationship;
    public FollowStatesDTO followStates;
    @Nullable public String criteria;

    @Nullable public GetPositionsDTOKey getGetPositionsDTOKey()
    {
        GetPositionsDTOKey key = getLeaderboardMarkUserId();
        if (key != null)
        {
            return key;
        }
        return getOwnedPortfolioId();
    }

    @Nullable public LeaderboardMarkUserId getLeaderboardMarkUserId()
    {
        if (lbmuId > 0)
        {
            return new LeaderboardMarkUserId((int) lbmuId);
        }
        return null;
    }

    @Nullable public OwnedPortfolioId getOwnedPortfolioId()
    {
        if (id > 0 && portfolioId > 0)
        {
            return new OwnedPortfolioId(id, portfolioId);
        }
        return null;
    }

    @NonNull public LeaderboardUserId getLeaderboardUserId()
    {
        return new LeaderboardUserId(id, lbmuId);
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
        return (Integer) get(LEADERBOARD_USER_POSITION);
    }

    @JsonIgnore
    public void setLeaderboardId(Integer leaderboardId)
    {
        this.put(LEADERBOARD_ID, leaderboardId);
    }

    @JsonIgnore
    public Integer getLeaderboardId()
    {
        return (Integer) get(LEADERBOARD_ID);
    }

    @JsonIgnore
    public Boolean isIncludeFoF()
    {
        return (Boolean) get(LEADERBOARD_INCLUDE_FOF);
    }

    @JsonIgnore
    public void setIncludeFoF(boolean includeFoF)
    {
        this.put(LEADERBOARD_INCLUDE_FOF, includeFoF);
    }

    @JsonIgnore
    @Override public boolean isExpanded()
    {
        return (Boolean) get(ExpandableItem.class.getName(), false);
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

