package com.tradehero.th.fragments.competition.zone.dto;

public class CompetitionZoneWizardDTO extends CompetitionZoneDTO
{
    public CompetitionZoneWizardDTO(String title, String description)
    {
        super(title, description);
    }

    @Override public String toString()
    {
        return "CompetitionZoneWizardDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
