package com.tradehero.th.api.leaderboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.utils.SecurityUtils;
import java.util.Date;
import java.util.List;

public abstract class BaseLeaderboardUserDTO extends UserBaseDTO
        implements ExpandableItem
{
    private static final String LEADERBOARD_USER_POSITION = "LEADERBOARD_USER_POSITION";
    private static final String LEADERBOARD_ID = "LEADERBOARD_ID";
    private static final String LEADERBOARD_INCLUDE_FOF = "LEADERBOARD_INCLUDE_FOF";

    public long lbmuId;    // leaderboardMarkUser.id ..., will be null if user not ranked
    public int portfolioId;    // ...OR portfolioId --> messy

    public double roiInPeriod;
    public double roiAnnualizedInPeriod;
    public int ordinalPosition; // OK
    public String currencyDisplay;
    public String currencyISO;

    @JsonProperty("friendOf_UserIds")
    public List<Integer> friendOfUserIds;    // client expects userIds here to be present in LeaderboardDTO.users collection!
    @JsonProperty("friendOf_markupString")
    public String friendOfMarkupString;

    public Date periodStartUtc;
    public Date periodEndUtc;

    @NonNull public LeaderboardUserId getLeaderboardUserId()
    {
        return new LeaderboardUserId(id, lbmuId);
    }

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
}
