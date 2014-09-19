package com.tradehero.th.api.achievement;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class AchievementShareReqFormDTO
{
    @NotNull public final List<SocialNetworkEnum> networks;

    //<editor-fold desc="Constructors">
    public AchievementShareReqFormDTO(@NotNull List<SocialNetworkEnum> networks)
    {
        this.networks = networks;
    }
    //</editor-fold>
}
