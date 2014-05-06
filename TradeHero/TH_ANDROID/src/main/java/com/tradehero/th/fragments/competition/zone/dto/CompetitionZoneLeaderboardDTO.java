package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.competition.CompetitionDTO;


public class CompetitionZoneLeaderboardDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZoneLeaderboardDTO.class.getSimpleName();

    public CompetitionDTO competitionDTO;

    public CompetitionZoneLeaderboardDTO(String title, String description, CompetitionDTO competitionDTO)
    {
        super(title, description);
        this.competitionDTO = competitionDTO;
    }

    public Boolean isActive()
    {
        if (competitionDTO == null || competitionDTO.leaderboard == null)
        {
            return null;
        }
        return competitionDTO.leaderboard.isWithinUtcRestricted();
    }
}
