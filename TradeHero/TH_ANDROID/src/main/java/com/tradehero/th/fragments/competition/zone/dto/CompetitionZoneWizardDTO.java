package com.ayondo.academy.fragments.competition.zone.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.ProviderDisplayCellDTO;

public class CompetitionZoneWizardDTO extends CompetitionZoneDTO
{
    @Nullable private final String webUrl;

    //<editor-fold desc="Constructors">
    public CompetitionZoneWizardDTO(@NonNull ProviderDisplayCellDTO providerDisplayCellDTO, @NonNull Resources resources)
    {
        this(providerDisplayCellDTO.title,
                providerDisplayCellDTO.subtitle,
                providerDisplayCellDTO.getNonEmptyImageUrl(),
                providerDisplayCellDTO.extractRedirectUrl(resources));
    }

    public CompetitionZoneWizardDTO(
            @Nullable String title,
            @Nullable String description,
            @Nullable String iconUrl,
            @Nullable String webUrl)
    {
        super(title, description, iconUrl, R.drawable.wizard);
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

    @Nullable public String getWebUrl()
    {
        return webUrl;
    }
}
