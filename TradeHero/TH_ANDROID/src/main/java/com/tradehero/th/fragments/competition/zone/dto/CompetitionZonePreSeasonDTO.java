package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.competition.CompetitionPreSeasonDTO;

public class CompetitionZonePreSeasonDTO extends CompetitionZoneDTO
{
    public String iconUrl;

    public CompetitionZonePreSeasonDTO(CompetitionPreSeasonDTO competitionPreSeasonDTO)
    {
        super(competitionPreSeasonDTO.title, competitionPreSeasonDTO.description);
        this.iconUrl = competitionPreSeasonDTO.prizeImageUrl;
    }
}
