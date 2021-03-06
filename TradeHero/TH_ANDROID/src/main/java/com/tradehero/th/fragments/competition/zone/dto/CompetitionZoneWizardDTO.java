package com.tradehero.th.fragments.competition.zone.dto;

public class CompetitionZoneWizardDTO extends CompetitionZoneDTO
{
    private final String iconUrl;
    private final String webUrl;

    //<editor-fold desc="Constructors">
    public CompetitionZoneWizardDTO(String title, String description, String iconUrl, String webUrl)
    {
        super(title, description);
        this.iconUrl = iconUrl;
        this.webUrl = webUrl;
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "CompetitionZoneWizardDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getWebUrl()
    {
        return webUrl;
    }
}
