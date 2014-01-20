package com.tradehero.th.fragments.competition.zone;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionZoneWizardDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZoneWizardDTO.class.getSimpleName();

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
