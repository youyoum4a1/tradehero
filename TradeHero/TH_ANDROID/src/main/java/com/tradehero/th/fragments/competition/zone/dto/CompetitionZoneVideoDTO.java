package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.Nullable;

public class CompetitionZoneVideoDTO extends CompetitionZoneDTO
{
    //<editor-fold desc="Constructors">
    public CompetitionZoneVideoDTO(@Nullable String title, @Nullable String description)
    {
        super(title, description);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "CompetitionZoneVideoDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
