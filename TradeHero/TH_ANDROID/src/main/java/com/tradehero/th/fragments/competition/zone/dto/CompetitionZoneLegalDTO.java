package com.ayondo.academy.fragments.competition.zone.dto;

import android.support.annotation.Nullable;
import com.ayondo.academy.R;

public class CompetitionZoneLegalDTO extends CompetitionZoneDTO
{
    //<editor-fold desc="Constructors">
    public CompetitionZoneLegalDTO(@Nullable String title, @Nullable String description)
    {
        super(title, description, null, R.drawable.default_image);
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
