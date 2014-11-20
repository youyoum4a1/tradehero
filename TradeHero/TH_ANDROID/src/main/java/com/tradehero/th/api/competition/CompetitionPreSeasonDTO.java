package com.tradehero.th.api.competition;

import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneDTO;

public class CompetitionPreSeasonDTO extends CompetitionZoneDTO
{
    public int providerId;
    public String headline;
    public String content;
    public String tncUrl;
    public String prizeImageUrl;

    public CompetitionPreSeasonDTO(String title, String description)
    {
        super(title, description);
    }
}
