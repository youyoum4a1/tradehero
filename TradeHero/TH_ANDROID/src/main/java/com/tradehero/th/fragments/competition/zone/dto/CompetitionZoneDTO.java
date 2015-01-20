package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;

public class CompetitionZoneDTO implements DTO
{
    @Nullable public final String title;
    @Nullable public final String description;

    //<editor-fold desc="Constructors">
    public CompetitionZoneDTO(@Nullable String title, @Nullable String description)
    {
        this.title = title;
        this.description = description;
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
