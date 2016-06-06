package com.androidth.general.fragments.competition.zone.dto;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.common.persistence.DTO;

public class CompetitionZoneDTO implements DTO
{
    @Nullable public final String title;
    @ViewVisibilityValue public final int descriptionVisibility;
    @Nullable public final String description;
    @Nullable public final String zoneIconUrl;
    @DrawableRes public final int zoneIconResId;

    //<editor-fold desc="Constructors">
    public CompetitionZoneDTO(
            @Nullable String title,
            @Nullable String description,
            @Nullable String zoneIconUrl,
            @DrawableRes int zoneIconResId)
    {
        this.title = title;

        descriptionVisibility = description == null ||
                description.length() == 0 ? View.GONE : View.VISIBLE;
        this.description = description;

        this.zoneIconUrl = zoneIconUrl;
        this.zoneIconResId = zoneIconResId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (title == null ? Integer.valueOf(0) : title).hashCode() ^
                (description == null ? Integer.valueOf(0) : description).hashCode();
    }

    @Override public String toString()
    {
        return "CompetitionZoneDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
