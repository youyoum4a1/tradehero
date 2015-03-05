package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.Nullable;

public class CompetitionZoneLegalDTO extends CompetitionZoneDTO
{
    //<editor-fold desc="Constructors">
    public CompetitionZoneLegalDTO(@Nullable String title, @Nullable String description)
    {
        super(title, description);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "CompetitionZoneLegalDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
