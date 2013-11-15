package com.tradehero.th.api.leaderboard;

import com.tradehero.th.fragments.leaderboard.LeaderboardSortType;
import com.tradehero.th.loaders.AbstractItemWithComparableId;
import java.util.Date;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:05 PM Copyright (c) TradeHero */
public class LeaderboardDefDTO extends AbstractItemWithComparableId<Integer>
{
    // TODO HARDCODED
    public static final int LEADERBOARD_DEF_MOST_SKILLED_ID = 49;

    // For fake leaderboard definition, hardcoded on client side
    public static final int LEADERBOARD_DEF_SECTOR_ID = -2;
    public static final int LEADERBOARD_DEF_EXCHANGE_ID = -3;
    public static final int LEADERBOARD_FRIEND_ID = -1;
    public static final String LEADERBOARD_DEF_DESC = "LEADERBOARD_DEF_DESC";
    public static final String LEADERBOARD_DEF_TIME_RESTRICTED = "LEADERBOARD_DEF_TIME_RESTRICTED";

    public int id;
    public String name;

    // LB with no restrictions here is global king of kings LB
    public boolean sectorRestrictions;
    public boolean exchangeRestrictions;
    public Date fromUtcRestricted;
    public Date toUtcRestricted;
    public Integer toDateDays;

    // count of # of users in most recent LB mark; zero here means client should not display the empty LB
    public int countLeaderboardEntries;

    // description String
    public String desc;

    // sort & cap fields
    public List<LeaderboardSortTypeDTO> sortTypes;
    public Integer defaultSortTypeId;
    public Integer capAt;

    public LeaderboardDefDTO()
    {
        super();
    }

    //<editor-fold desc="AbstractItemWithComparableId">
    @Override public Integer getId()
    {
        return id;
    }

    @Override public void setId(Integer id)
    {
        this.id = id;
    }
    //</editor-fold>

    public boolean isTimeRestrictedLeaderboard()
    {
        return (this.fromUtcRestricted != null && this.toUtcRestricted != null) || (this.toDateDays != null && this.toDateDays > 0);
    }

    public boolean isUnrestrictedLeaderboard()
    {
        return !this.exchangeRestrictions &&
                !this.sectorRestrictions &&
                (this.fromUtcRestricted == null) &&
                (this.toUtcRestricted == null) &&
                (this.toDateDays == 0);
    }

    public LeaderboardSortTypeDTO defaultSortType()
    {
        for (LeaderboardSortTypeDTO sortTypeDTO: sortTypes)
        {
            if (sortTypeDTO.sortTypeId == defaultSortTypeId)
            {
                return sortTypeDTO;
            }
        }
        return null;
    }

    public int getSortOptionFlags()
    {
        switch (id)
        {
            case LEADERBOARD_DEF_MOST_SKILLED_ID:
                return LeaderboardSortType.HeroQuotient.getFlag()
                        | LeaderboardSortType.Roi.getFlag()
                        | LeaderboardSortType.Followers.getFlag()
                        | LeaderboardSortType.Comments.getFlag()
                        | LeaderboardSortType.SharpeRatio.getFlag();
            default:
                return LeaderboardSortType.HeroQuotient.getFlag()
                        | LeaderboardSortType.Roi.getFlag();
        }
    }

    public LeaderboardSortType getDefaultSortType()
    {
        return defaultSortTypeId != null ? LeaderboardSortType.byServerFlag(defaultSortTypeId) : LeaderboardSortType.DefaultSortType;
    }

    public boolean isSectorRestricted()
    {
        return sectorRestrictions;
    }

    public boolean isExchangeRestricted()
    {
        return exchangeRestrictions;
    }

    // TODO datetime format
    public String getPeriodStartString()
    {
        return fromUtcRestricted != null ? fromUtcRestricted.toString() : null;
    }

    // TODO datetime format
    public String getPeriodEndString()
    {
        return toUtcRestricted != null ? toUtcRestricted.toString() : null;
    }

    public Integer getRank()
    {
        LeaderboardSortType currentSortType = (LeaderboardSortType) get(LeaderboardSortType.TAG);
        if (currentSortType == null)
        {
            currentSortType = getDefaultSortType();
        }

        if (sortTypes == null)
        {
            return null;
        }

        for (LeaderboardSortTypeDTO sortTypeDTO: sortTypes)
        {
            if (sortTypeDTO.sortTypeId == currentSortType.getServerFlag())
            {
                if (sortTypeDTO.userRankingOrdinalPosition != null)
                {
                    return sortTypeDTO.userRankingOrdinalPosition + 1;
                }
            }
        }
        return null;
    }
}


