package com.ayondo.academy.fragments.competition.zone.dto;

import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.CompetitionPreSeasonDTO;

public class CompetitionZonePreSeasonDTO extends CompetitionZoneDTO
{
    public String iconUrl;

    public CompetitionZonePreSeasonDTO(@NonNull CompetitionPreSeasonDTO competitionPreSeasonDTO)
    {
        super(competitionPreSeasonDTO.title,
                competitionPreSeasonDTO.description,
                competitionPreSeasonDTO.getNonEmptyPrizeImageUrl(),
                R.drawable.default_image);
        this.iconUrl = competitionPreSeasonDTO.prizeImageUrl;
    }
}
