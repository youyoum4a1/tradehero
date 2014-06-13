package com.tradehero.th.fragments.competition.zone.dto;

public class CompetitionZoneLegalDTO extends CompetitionZoneDTO
{
    public LinkType requestedLink = null;

    public CompetitionZoneLegalDTO(String title, String description)
    {
        super(title, description);
    }

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
