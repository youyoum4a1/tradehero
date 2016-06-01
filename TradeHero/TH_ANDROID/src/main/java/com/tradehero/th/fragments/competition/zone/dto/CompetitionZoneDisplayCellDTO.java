package com.ayondo.academy.fragments.competition.zone.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.ProviderDisplayCellDTO;

public class CompetitionZoneDisplayCellDTO extends CompetitionZoneDTO
{
    @NonNull protected ProviderDisplayCellDTO providerDisplayCellDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZoneDisplayCellDTO(@NonNull ProviderDisplayCellDTO providerDisplayCellDTO)
    {
        super(providerDisplayCellDTO.title,
                providerDisplayCellDTO.subtitle,
                providerDisplayCellDTO.getNonEmptyImageUrl(),
                R.drawable.default_image);
        this.providerDisplayCellDTO = providerDisplayCellDTO;
    }
    //</editor-fold>

    @NonNull public ProviderDisplayCellDTO getProviderDisplayCellDTO()
    {
        return providerDisplayCellDTO;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                providerDisplayCellDTO.getProviderDisplayCellId().hashCode();
    }

    @Override public boolean equals(@Nullable Object o)
    {
        if (o == null)
        {
            return false;
        }
        if (o == this)
        {
            return true;
        }
        if (o instanceof CompetitionZoneDisplayCellDTO)
        {
            return ((CompetitionZoneDisplayCellDTO) o).getProviderDisplayCellDTO().equals(this.providerDisplayCellDTO);
        }
        return false;
    }

    @Override public String toString()
    {
        return "CompetitionZoneDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Nullable public String extractRedirectUrl(@NonNull Resources resources)
    {
        return providerDisplayCellDTO.extractRedirectUrl(resources);
    }
}
