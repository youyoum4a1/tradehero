package com.tradehero.th.api.achievement;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;
import android.support.annotation.NonNull;

public class AchievementShareReqFormDTO
{
    @NonNull public final List<SocialNetworkEnum> networks;

    //<editor-fold desc="Constructors">
    public AchievementShareReqFormDTO(@NonNull List<SocialNetworkEnum> networks)
    {
        this.networks = networks;
    }
    //</editor-fold>
}
