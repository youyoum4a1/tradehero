package com.tradehero.th.fragments.competition.zone.dto;


public class CompetitionZoneVideoDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZoneVideoDTO.class.getSimpleName();

    public CompetitionZoneVideoDTO(String title, String description)
    {
        super(title, description);
    }

    @Override public String toString()
    {
        return "CompetitionZoneVideoDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
