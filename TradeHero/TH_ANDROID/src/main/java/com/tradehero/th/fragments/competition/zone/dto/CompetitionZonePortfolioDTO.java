package com.tradehero.th.fragments.competition.zone.dto;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionZonePortfolioDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZonePortfolioDTO.class.getSimpleName();

    public CompetitionZonePortfolioDTO(String title, String description)
    {
        super(title, description);
    }

    @Override public String toString()
    {
        return "CompetitionZonePortfolioDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
