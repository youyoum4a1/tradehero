package com.androidth.general.fragments.contestcenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.competition.ProviderDTO;

class ProviderContestPageDTO implements ContestPageDTO
{
    @NonNull public final ProviderDTO providerDTO;

    //<editor-fold desc="Constructors">
    public ProviderContestPageDTO(@NonNull ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return providerDTO.getProviderId().hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
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
