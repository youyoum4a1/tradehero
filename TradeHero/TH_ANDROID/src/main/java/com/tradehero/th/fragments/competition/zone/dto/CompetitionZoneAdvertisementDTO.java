package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.AdDTO;

public class CompetitionZoneAdvertisementDTO extends CompetitionZoneDTO
{
    @Nullable private final AdDTO adDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZoneAdvertisementDTO(@Nullable AdDTO adDTO)
    {
        super(null, null);
        this.adDTO = adDTO;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (adDTO == null ? 0 : adDTO.bannerImageUrl == null ? Integer.valueOf(0) : adDTO.bannerImageUrl).hashCode();
    }

    @Nullable public AdDTO getAdDTO()
    {
        return adDTO;
    }

    @Override public String toString()
    {
        return "CompetitionZoneAdvertisementDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + (adDTO == null ? "null" : adDTO.bannerImageUrl) + '\'' +
                '}';
    }
}
