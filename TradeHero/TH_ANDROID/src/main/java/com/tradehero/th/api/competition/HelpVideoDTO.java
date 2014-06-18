package com.tradehero.th.api.competition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import org.jetbrains.annotations.NotNull;

public class HelpVideoDTO implements DTO
{
    public int id;
    public String title;
    public String subtitle;
    public String thumbnailUrl;
    public String videoUrl;
    public Integer providerId;
    public String embedCode;

    @JsonIgnore
    @NotNull public HelpVideoId getHelpVideoId()
    {
        return new HelpVideoId(id);
    }

    @JsonIgnore
    @NotNull public ProviderId getProviderId()
    {
        return new ProviderId(providerId);
    }

    @Override public String toString()
    {
        return "HelpVideoDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", providerId=" + providerId +
                ", embedCode='" + embedCode + '\'' +
                '}';
    }
}
