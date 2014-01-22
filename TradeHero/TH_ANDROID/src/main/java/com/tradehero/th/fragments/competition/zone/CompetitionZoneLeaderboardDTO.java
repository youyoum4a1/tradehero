package com.tradehero.th.fragments.competition.zone;

import com.tradehero.th.api.competition.CompetitionDTO;

/**
 * Created by xavier on 1/22/14.
 */
public class CompetitionZoneLeaderboardDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZoneLeaderboardDTO.class.getSimpleName();

    public CompetitionDTO competitionDTO;

    public CompetitionZoneLeaderboardDTO(String title, String description, CompetitionDTO competitionDTO)
    {
        super(title, description);
        this.competitionDTO = competitionDTO;
    }
}
