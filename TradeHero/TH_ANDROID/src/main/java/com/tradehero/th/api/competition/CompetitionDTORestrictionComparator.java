package com.ayondo.academy.api.competition;

import java.io.Serializable;
import java.util.Comparator;

public class CompetitionDTORestrictionComparator implements Comparator<CompetitionDTO>, Serializable
{
    @Override public int compare(CompetitionDTO lhs, CompetitionDTO rhs)
    {
        if (lhs.leaderboard == null || rhs.leaderboard == null)
        {
            return 0;
        }
        if (!lhs.leaderboard.isTimeRestrictedLeaderboard() || !rhs.leaderboard.isTimeRestrictedLeaderboard())
        {
            return 0;
        }

        if (lhs.leaderboard.getTimeRestrictionRangeInMillis() == 0 || rhs.leaderboard.getTimeRestrictionRangeInMillis() == 0)
        {
            return 0;
        }

        int compare = rhs.leaderboard.toUtcRestricted.compareTo(lhs.leaderboard.toUtcRestricted);
        if (compare == 0)
        {
            compare = rhs.leaderboard.getTimeRestrictionRangeInMillis() > lhs.leaderboard.getTimeRestrictionRangeInMillis()? 1 : -1;
        }
        return compare;
    }
}