package com.tradehero.th.api.achievement;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class AchievementShareRequestDTO
{
    @NotNull public final List<SocialNetworkEnum> networks;

    public AchievementShareRequestDTO(@NotNull List<SocialNetworkEnum> networks)
    {
        this.networks = networks;
    }
}
