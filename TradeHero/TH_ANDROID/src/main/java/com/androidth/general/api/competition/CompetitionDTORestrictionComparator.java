package com.androidth.general.api.competition;

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

        //if(!lhs.competitionDurationType.equals(rhs.competitionDurationType)){
        //    int lDtoIntMapping = intMapping(lhs);
        //    int rDtoIntMapping = intMapping(rhs);
        //    return rDtoIntMapping - lDtoIntMapping;
        //}
        //else {
        //    return lhs.leaderboard.fromUtcRestricted.compareTo(rhs.leaderboard.fromUtcRestricted);
        //}

        return lhs.leaderboard.sortOrder.compareTo(rhs.leaderboard.sortOrder);

    }
    private int intMapping(CompetitionDTO dto){
        final int WEEKLY = 7;
        final int MONTHLY = 31;
        final int FORTNIGHTLY = 14;
        final int OVERALL = Integer.MAX_VALUE;

        if(dto.competitionDurationType.equals("Overall")){
            return OVERALL;
        }
        else if(dto.competitionDurationType.equals("Monthly")){
            return MONTHLY;
        }
        else if(dto.competitionDurationType.equals("Fortnightly")){
            return FORTNIGHTLY;
        }
        else return WEEKLY;

    }
}