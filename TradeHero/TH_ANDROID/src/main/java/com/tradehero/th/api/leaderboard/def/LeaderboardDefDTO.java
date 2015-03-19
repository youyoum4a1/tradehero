package com.tradehero.th.api.leaderboard.def;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.leaderboard.CountryCodeList;
import com.tradehero.th.api.leaderboard.key.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class LeaderboardDefDTO extends ExtendedDTO
{
    public int id;
    public String name;

    // LB with no restrictions here is global king of kings LB
    public boolean sectorRestrictions;
    public boolean exchangeRestrictions;
    public Date fromUtcRestricted;
    public Date toUtcRestricted;
    public Integer toDateDays;


    // description String
    public String desc;

    public CountryCodeList countryCodes;

    public LeaderboardDefDTO()
    {
        super();
    }

    @NotNull public LeaderboardDefKey getLeaderboardDefKey()
    {
        return new LeaderboardDefKey(id);
    }

    @NotNull public LeaderboardDefListKey getLeaderboardDefListKey()
    {
        if (exchangeRestrictions)
        {
            return new ExchangeLeaderboardDefListKey();
        }
        if (sectorRestrictions)
        {
            return new SectorLeaderboardDefListKey();
        }
        if (isTimeRestrictedLeaderboard())
        {
            return new TimePeriodLeaderboardDefListKey();
        }
        throw new IllegalStateException("Unhandled situation " + this);
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

    public Boolean isWithinUtcRestricted()
    {
        return isWithinUtcRestricted(new Date());
    }

    public Boolean isWithinUtcRestricted(Date now)
    {
        return (fromUtcRestricted == null || now.equals(fromUtcRestricted) || now.after(fromUtcRestricted)) &&
                (toUtcRestricted == null || now.equals(toUtcRestricted) || now.before(toUtcRestricted));
    }
}

