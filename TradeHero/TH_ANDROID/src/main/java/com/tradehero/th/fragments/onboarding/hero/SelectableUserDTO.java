package com.tradehero.th.fragments.onboarding.hero;

import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import org.jetbrains.annotations.NotNull;

public class SelectableUserDTO extends SelectableDTO<LeaderboardUserDTO>
{
    //<editor-fold desc="Constructors">
    SelectableUserDTO(@NotNull LeaderboardUserDTO user)
    {
        super(user);
    }
    //</editor-fold>
}
