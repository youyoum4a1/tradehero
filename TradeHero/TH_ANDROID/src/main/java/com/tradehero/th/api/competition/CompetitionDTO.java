package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.key.CompetitionId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import java.util.Comparator;
import org.jetbrains.annotations.Nullable;

public class CompetitionDTO implements DTO
{
    public int id;
    @Nullable public LeaderboardDefDTO leaderboard;
    public String name;
    public String competitionDurationType;
    public String iconActiveUrl;
    public String iconInactiveUrl;
    public String prizeValueWithCcy;
    @Nullable public LeaderboardUserDTO leaderboardUser;

    //<editor-fold desc="Constructors">
    public CompetitionDTO()
    {
        super();
    }

    public CompetitionDTO(int id, @Nullable LeaderboardDefDTO leaderboard, String name, String competitionDurationType, String iconActiveUrl,
            String iconInactiveUrl, String prizeValueWithCcy, LeaderboardUserDTO leaderboardUser)
    {
        this.id = id;
        this.leaderboard = leaderboard;
        this.name = name;
        this.competitionDurationType = competitionDurationType;
        this.iconActiveUrl = iconActiveUrl;
        this.iconInactiveUrl = iconInactiveUrl;
        this.prizeValueWithCcy = prizeValueWithCcy;
        this.leaderboardUser = leaderboardUser;
    }
    //</editor-fold>

    public CompetitionId getCompetitionId()
    {
        return new CompetitionId(id);
    }

    public String getIconUrl()
    {
        LeaderboardDefDTO leaderboardCopy = this.leaderboard;
        if (leaderboardCopy != null)
        {
            Boolean isWithinUtcRestricted = leaderboardCopy.isWithinUtcRestricted();
            if (isWithinUtcRestricted != null && isWithinUtcRestricted)
            {
                return iconActiveUrl;
            }
            else if (isWithinUtcRestricted != null)
            {
                return iconInactiveUrl;
            }
        }
        return null;
    }

    public static final Comparator<CompetitionDTO> RestrictionLeaderboardComparator = new Comparator<CompetitionDTO>()
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

            int compare = (int) (rhs.leaderboard.getTimeRestrictionRangeInMillis() - lhs.leaderboard.getTimeRestrictionRangeInMillis());
            if (compare == 0)
            {
                compare = rhs.leaderboard.fromUtcRestricted.compareTo(lhs.leaderboard.fromUtcRestricted);
                if (compare == 0)
                {
                    compare = rhs.leaderboard.toUtcRestricted.compareTo(lhs.leaderboard.toUtcRestricted);
                }
            }
            return compare;
        }
    };
}
