package com.tradehero.th.api.leaderboard;

/** Created with IntelliJ IDEA. User: tho Date: 10/14/13 Time: 2:05 PM Copyright (c) TradeHero */

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.loaders.AbstractItemWithComparableId;
import java.util.Date;
import java.util.List;

public class LeaderboardDefDTO extends AbstractItemWithComparableId<Integer>
    implements DTO
{
    // TODO HARDCODED
    public static final int LEADERBOARD_DEF_MOST_SKILLED_ID = 49;

    // For fake leaderboard definition, hardcoded on client side
    public static final int LEADERBOARD_DEF_SECTOR_ID = -1;
    public static final int LEADERBOARD_DEF_EXCHANGE_ID = -2;

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

    @Override public Integer getId()
    {
        return id;
    }

    @Override public void setId(Integer id)
    {
        this.id = id;
    }

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

}


