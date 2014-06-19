package com.tradehero.th.api.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.social.InviteDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.SecurityUtils;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class LeaderboardUserDTO extends UserBaseDTO
    implements ExpandableItem
{
    public static final String LEADERBOARD_PERIOD_START_STRING = "LEADERBOARD_PERIOD_START_STRING";
    private static final String LEADERBOARD_USER_POSITION = "LEADERBOARD_USER_POSITION";
    private static final String LEADERBOARD_ID = "LEADERBOARD_ID";
    private static final String LEADERBOARD_INCLUDE_FOF = "LEADERBOARD_INCLUDE_FOF";
    public static final Double MIN_CONSISTENCY = 0.004;

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
    @Nullable public Double avgNumberOfTradesPerMonth;

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

    //for social friends
    public String name;
    public String thUserId;
    public String fbId;
    public String liId;
    public String twId;
    public String wbId;
    public String fbPicUrl;
    public String liPicUrl;
    public String twPicUrl;
    public String wbPicUrl;
    public boolean alreadyInvited;

    public LeaderboardUserDTO()
    {
        super();
    }

    public LeaderboardMarkUserId getLeaderboardMarkUserId()
    {
        return new LeaderboardMarkUserId((int) lbmuId);
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

    public String getPicture()
    {
        if (picture != null)
        {
            return picture;
        }
        else if (fbPicUrl != null)
        {
            return fbPicUrl;
        }
        else if (liPicUrl != null)
        {
            return liPicUrl;
        }
        else if (twPicUrl != null)
        {
            return twPicUrl;
        }
        else if (wbPicUrl != null)
        {
            return wbPicUrl;
        }
        return null;
    }

    public Integer getLableRes()
    {
        if (fbId != null)
        {
            return R.drawable.icon_share_fb_on;
        }
        else if (liId != null)
        {
            return R.drawable.icon_share_linkedin_on;
        }
        else if (twId != null)
        {
            return R.drawable.icon_share_tw_on;
        }
        else if (wbId != null)
        {
            return R.drawable.icn_weibo_round;
        }
        return null;
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

    public InviteDTO getInviteDTO()
    {
        InviteDTO inviteDTO = new InviteDTO();
        if (liId != null && !liId.isEmpty())
        {
            inviteDTO.liId = liId;
        }
        else if (twId != null && !twId.isEmpty())
        {
            inviteDTO.twId = twId;
        }
        return inviteDTO;
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

