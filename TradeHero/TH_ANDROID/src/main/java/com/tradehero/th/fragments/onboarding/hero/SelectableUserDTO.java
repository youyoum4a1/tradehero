package com.tradehero.th.fragments.onboarding.hero;

import android.support.annotation.NonNull;
import com.tradehero.common.api.SelectableDTO;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;

public class SelectableUserDTO extends SelectableDTO<StocksLeaderboardUserDTO>
{
    //<editor-fold desc="Constructors">
    SelectableUserDTO(@NonNull StocksLeaderboardUserDTO user)
    {
        super(user);
    }
    //</editor-fold>
}
