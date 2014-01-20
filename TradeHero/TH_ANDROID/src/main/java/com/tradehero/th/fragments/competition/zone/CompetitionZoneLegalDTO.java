package com.tradehero.th.fragments.competition.zone;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionZoneLegalDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZoneLegalDTO.class.getSimpleName();

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
