package com.tradehero.th.fragments.competition.zone.dto;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionPreSeasonDTO;

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
