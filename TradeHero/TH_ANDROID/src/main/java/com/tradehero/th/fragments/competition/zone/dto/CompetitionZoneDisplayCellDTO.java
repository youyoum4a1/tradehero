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

    @Override public int hashCode()
    {
        return super.hashCode() ^
                providerDisplayCellDTO.getProviderDisplayCellId().hashCode() ^
                providerDisplayCellDTO.redirectUrl.hashCode();
    }

    @Override public String toString()
    {
        return "CompetitionZoneDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getIconUrl()
    {
        return providerDisplayCellDTO.imageUrl;
    }

    public String getRedirectUrl()
    {
        return providerDisplayCellDTO.redirectUrl;
    }
}
