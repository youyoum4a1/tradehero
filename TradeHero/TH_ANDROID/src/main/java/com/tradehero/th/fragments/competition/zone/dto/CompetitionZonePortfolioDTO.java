package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.th.api.users.UserProfileCompactDTO;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionZonePortfolioDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZonePortfolioDTO.class.getSimpleName();

    public UserProfileCompactDTO userProfileCompactDTO;

    public CompetitionZonePortfolioDTO(String title, String description, UserProfileCompactDTO userProfileCompactDTO)
    {
        super(title, description);
        this.userProfileCompactDTO = userProfileCompactDTO;
    }

    @Override public String toString()
    {
        return "CompetitionZonePortfolioDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userProfileCompactDTO='" + userProfileCompactDTO + '\'' +
                '}';
    }
}
