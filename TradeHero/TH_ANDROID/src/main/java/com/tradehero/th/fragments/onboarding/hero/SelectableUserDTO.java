package com.ayondo.academy.fragments.onboarding.hero;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.ayondo.academy.api.leaderboard.LeaderboardUserDTO;

public class SelectableUserDTO extends SelectableDTO<LeaderboardUserDTO>
{
    //<editor-fold desc="Constructors">
    SelectableUserDTO(@NonNull LeaderboardUserDTO user)
    {
        super(user);
    }

    public SelectableUserDTO(@NonNull LeaderboardUserDTO value, boolean selected)
    {
        super(value, selected);
    }
    //</editor-fold>
}
