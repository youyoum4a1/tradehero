package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.competition.CompetitionDTO;

public class CompetitionZoneLeaderboardDTO extends CompetitionZoneDTO
{
    public final CompetitionDTO competitionDTO;

    //<editor-fold desc="Constructors">
    public CompetitionZoneLeaderboardDTO(String title, String description, CompetitionDTO competitionDTO)
    {
        super(title, description);
        this.competitionDTO = competitionDTO;
    }
    //</editor-fold>

    public Boolean isActive()
    {
        if (competitionDTO == null || competitionDTO.leaderboard == null)
        {
            return null;
        }
        return competitionDTO.leaderboard.isWithinUtcRestricted();
    }
}
