package com.tradehero.th.fragments.onboarding.hero;

import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import android.support.annotation.NonNull;

public class SelectableUserDTO extends SelectableDTO<LeaderboardUserDTO>
{
    //<editor-fold desc="Constructors">
    SelectableUserDTO(@NonNull LeaderboardUserDTO user)
    {
        super(user);
    }
    //</editor-fold>
}
