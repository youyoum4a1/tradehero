package com.tradehero.th.fragments.leaderboard.main;

import com.tradehero.th.api.competition.ProviderDTO;
import org.jetbrains.annotations.NotNull;

class ProviderCommunityPageDTO implements CommunityPageDTO
{
    @NotNull public final ProviderDTO providerDTO;

    //<editor-fold desc="Constructors">
    ProviderCommunityPageDTO(@NotNull ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return providerDTO.getProviderId().hashCode();
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
        return this.providerDTO.getProviderId().equals(((ProviderCommunityPageDTO) other).providerDTO.getProviderId());
    }
}
