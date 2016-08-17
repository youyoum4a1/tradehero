package com.androidth.general.api.leaderboard.def;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.leaderboard.CountryCodeList;
import com.androidth.general.api.leaderboard.LeaderboardSortTypeDTOList;
import com.androidth.general.api.leaderboard.key.LeaderboardDefKey;
import java.util.Date;

public class LeaderboardDefDTO implements DTO
{
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
    public LeaderboardSortTypeDTOList sortTypes;
    public Integer defaultSortTypeId;
    public Integer capAt;
    public CountryCodeList countryCodes;
    public Integer sortOrder;

    //<editor-fold desc="Constructors">
    public LeaderboardDefDTO()
    {
        super();
    }
    //</editor-fold>

    @NonNull public LeaderboardDefKey getLeaderboardDefKey()
    {
        return new LeaderboardDefKey(id);
    }

    public boolean isTimeRestrictedLeaderboard()
    {
        return (this.fromUtcRestricted != null && this.toUtcRestricted != null) || (this.toDateDays != null && this.toDateDays > 0);
    }

    public long getTimeRestrictionRangeInMillis()
    {
        if(toUtcRestricted == null || fromUtcRestricted == null)
        {
            return 0;
        }
        return toUtcRestricted.getTime() - fromUtcRestricted.getTime();
    }

    public boolean isSectorRestricted()
    {
        return sectorRestrictions;
    }

    public boolean isExchangeRestricted()
    {
        return exchangeRestrictions;
    }

    @NonNull public Boolean isWithinUtcRestricted()
    {
        return isWithinUtcRestricted(new Date());
    }

    @NonNull public Boolean isWithinUtcRestricted(@NonNull Date now)
    {
        return (fromUtcRestricted == null || now.equals(fromUtcRestricted) || now.after(fromUtcRestricted)) &&
                (toUtcRestricted == null || now.equals(toUtcRestricted) || now.before(toUtcRestricted));
    }
}

