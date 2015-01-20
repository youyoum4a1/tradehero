package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.AdDTO;

public class CompetitionZoneAdvertisementDTO extends CompetitionZoneDTO
{
    @DrawableRes public final int imageResId;
    @Nullable private final AdDTO adDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZoneAdvertisementDTO(
            @Nullable String title,
            @Nullable String description,
            @DrawableRes int imageResId,
            @Nullable AdDTO adDTO)
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
                ", imageResId=" + imageResId +
                ", imageUrl='" + (adDTO == null ? "null" : adDTO.bannerImageUrl) + '\'' +
                '}';
    }
}
