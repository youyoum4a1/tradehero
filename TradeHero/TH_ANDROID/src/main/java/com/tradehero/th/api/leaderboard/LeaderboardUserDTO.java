package com.tradehero.th.api.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.api.leaderboard.key.LeaderboardUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeaderboardUserDTO extends UserBaseDTO
        implements ExpandableItem
{
    private static final String LEADERBOARD_USER_POSITION = "LEADERBOARD_USER_POSITION";
    public static final Double MIN_CONSISTENCY = 0.004;

    public long lbmuId;    // leaderboardMarkUser.id ...
    public int portfolioId;    // ...OR portfolioId --> messy

    public double roiInPeriod;

    public int ordinalPosition; // OK

    public String currencyDisplay;

    public int followerCount;
    public double totalWealth;
    public double perfRoi;
    public int tradeCount;
    public String exchange;
    public String securityName;
    public String symbol;
    public int watchCount;
    public int topWatchUserId;
    public String topWatchUserName;

    //for buy what
    public double monthlyRoi;
    public double price;
    public Date dateTimeUtc;
    public double percent;
    public double winRatio;

    public LeaderboardUserDTO()
    {
        super();
    }

    @Nullable public OwnedPortfolioId getOwnedPortfolioId()
    {
        if (id > 0 && portfolioId > 0)
        {
            return new OwnedPortfolioId(id, portfolioId);
        }
        return null;
    }

    @NotNull public LeaderboardUserId getLeaderboardUserId()
    {
        return new LeaderboardUserId(id, lbmuId);
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
    @Override public boolean isExpanded()
    {
        return (Boolean) get(ExpandableItem.class.getName(), false);
    }

    @JsonIgnore
    @Override public void setExpanded(boolean expanded)
    {
        this.put(ExpandableItem.class.getName(), expanded);
    }

    public String getShortTopWatchUserName(int length)
    {
        String name = topWatchUserName;
        if (name == null) {
            return " ";
        }
        if (name.length() > length) {
            name = name.substring(0, length - 1);
            name = name + "...";
        }

        return name;
    }

    public String getDisplayableWatchCount()
    {
        int tenKNum = watchCount / 10000;
        if (tenKNum < 1)
        {
            return String.valueOf(watchCount);
        }
        else
        {
            return tenKNum + "万多";
        }
    }

    //</editor-fold>
}

