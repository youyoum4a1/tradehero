package com.tradehero.th.fragments.contestcenter;

import com.tradehero.th.api.competition.ProviderDTO;
import org.jetbrains.annotations.NotNull;

class ProviderContestPageDTO implements ContestPageDTO
{
    @NotNull public final ProviderDTO providerDTO;

    //<editor-fold desc="Constructors">
    public ProviderContestPageDTO(@NotNull ProviderDTO providerDTO)
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
        if (!(other instanceof ProviderContestPageDTO))
        {
            return false;
        }
        return this.providerDTO.getProviderId().equals(((ProviderContestPageDTO) other).providerDTO.getProviderId());
    }
}
