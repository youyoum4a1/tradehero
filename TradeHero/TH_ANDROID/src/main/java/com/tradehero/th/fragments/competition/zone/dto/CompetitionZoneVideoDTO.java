package com.tradehero.th.fragments.competition.zone.dto;

public class CompetitionZoneVideoDTO extends CompetitionZoneDTO
{
    //<editor-fold desc="Constructors">
    public CompetitionZoneVideoDTO(String title, String description)
    {
        super(title, description);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "CompetitionZoneVideoDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
