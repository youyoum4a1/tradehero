package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.competition.AdDTO;

public class CompetitionZoneAdvertisementDTO extends CompetitionZoneDTO
{
    public final int imageResId;
    private final AdDTO adDTO;

    public CompetitionZoneAdvertisementDTO(String title, String description, int imageResId, AdDTO adDTO)
    {
        super(title, description);
        this.imageResId = imageResId;
        this.adDTO = adDTO;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                Integer.valueOf(imageResId).hashCode() ^
                (adDTO.bannerImageUrl == null ? Integer.valueOf(0) : adDTO.bannerImageUrl).hashCode();
    }

    public AdDTO getAdDTO()
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
