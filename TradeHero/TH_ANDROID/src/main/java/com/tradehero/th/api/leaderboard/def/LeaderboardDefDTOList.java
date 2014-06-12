package com.tradehero.th.api.leaderboard.def;

import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDefDTOList extends ArrayList<LeaderboardDefDTO>
{
    //<editor-fold desc="Contructors">
    public LeaderboardDefDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public LeaderboardDefDTOList()
    {
        super();
    }

    public LeaderboardDefDTOList(Collection<? extends LeaderboardDefDTO> c)
    {
        super(c);
    }
    //</editor-fold>

    @NotNull
    public LeaderboardDefDTOList keepForCountryCode(@NotNull String countryCode)
    {
        Predicate<LeaderboardDefDTO> leaderboardDefDTOPredicate = new LeaderboardDefDTOCountryCodeContainPredicate(countryCode);
        LeaderboardDefDTOList kept = new LeaderboardDefDTOList();
        for (LeaderboardDefDTO leaderboardDefDTO : this)
        {
            if (leaderboardDefDTOPredicate.apply(leaderboardDefDTO))
            {
                kept.add(leaderboardDefDTO);
            }
        }
        return kept;
    }
}
