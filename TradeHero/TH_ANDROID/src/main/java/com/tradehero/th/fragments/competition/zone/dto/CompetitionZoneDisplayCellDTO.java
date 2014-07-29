package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.competition.ProviderDisplayCellDTO;
import org.jetbrains.annotations.NotNull;

public class CompetitionZoneDisplayCellDTO extends CompetitionZoneDTO
{
    @NotNull protected ProviderDisplayCellDTO providerDisplayCellDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZoneDisplayCellDTO(@NotNull ProviderDisplayCellDTO providerDisplayCellDTO)
    {
        super(providerDisplayCellDTO.title, providerDisplayCellDTO.subtitle);
        this.providerDisplayCellDTO = providerDisplayCellDTO;
    }
    //</editor-fold>

    public ProviderDisplayCellDTO getProviderDisplayCellDTO()
    {
        return providerDisplayCellDTO;
    }

    public String getIconUrl()
    {
        return providerDisplayCellDTO.imageUrl;
    }

    public String getRedirectUrl()
    {
        return providerDisplayCellDTO.redirectUrl;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                providerDisplayCellDTO.getProviderDisplayCellId().hashCode();
    }

    @Override public boolean equals(Object o)
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
}
