package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.Nullable;

public class CompetitionZoneWizardDTO extends CompetitionZoneDTO
{
    @Nullable private final String iconUrl;
    @Nullable private final String webUrl;

    //<editor-fold desc="Constructors">
    public CompetitionZoneWizardDTO(
            @Nullable String title,
            @Nullable String description,
            @Nullable String iconUrl,
            @Nullable String webUrl)
    {
        super(title, description);
        this.iconUrl = iconUrl;
        this.webUrl = webUrl;
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "CompetitionZoneWizardDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Nullable public String getIconUrl()
    {
        return iconUrl;
    }

    @Nullable public String getWebUrl()
    {
        return webUrl;
    }
}
