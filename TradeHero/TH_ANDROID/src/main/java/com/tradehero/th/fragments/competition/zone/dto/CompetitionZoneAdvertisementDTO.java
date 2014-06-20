package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.competition.AdDTO;
import org.jetbrains.annotations.Nullable;

public class CompetitionZoneAdvertisementDTO extends CompetitionZoneDTO
{
    public final int imageResId;
    @Nullable private final AdDTO adDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZoneAdvertisementDTO(String title, String description, int imageResId, @Nullable AdDTO adDTO)
    {
        super(title, description);
        this.imageResId = imageResId;
        this.adDTO = adDTO;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                Integer.valueOf(imageResId).hashCode() ^
                (adDTO.bannerImageUrl == null ? Integer.valueOf(0) : adDTO.bannerImageUrl).hashCode();
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
                ", imageResId=" + imageResId +
                ", imageUrl='" + adDTO.bannerImageUrl + '\'' +
                '}';
    }
}
