package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.competition.ProviderId;
import org.jetbrains.annotations.NotNull;

class ProviderCommunityPageDTO implements CommunityPageDTO
{
    @NotNull
    public final ProviderId providerId;

    //<editor-fold desc="Constructors">
    ProviderCommunityPageDTO(@NotNull ProviderId providerId)
    {
        this.providerId = providerId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return providerId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof ProviderCommunityPageDTO))
        {
            return false;
        }
        if (this.providerId == ((ProviderCommunityPageDTO) other).providerId)
        {
            return true;
        }
        return this.providerId.equals(((ProviderCommunityPageDTO) other).providerId);
    }
}
