package com.tradehero.th.fragments.competition.zone.dto;

public class CompetitionZoneLegalDTO extends CompetitionZoneDTO
{
    public LinkType requestedLink = null;

    //<editor-fold desc="Constructors">
    public CompetitionZoneLegalDTO(String title, String description)
    {
        super(title, description);
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "CompetitionZoneLegalDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public enum LinkType
    {
        RULES,
        TERMS
    }
}
