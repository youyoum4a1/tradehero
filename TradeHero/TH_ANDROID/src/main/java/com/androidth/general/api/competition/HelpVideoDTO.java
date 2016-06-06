package com.androidth.general.api.competition;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.competition.key.HelpVideoId;

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
    @NonNull public HelpVideoId getHelpVideoId()
    {
        return new HelpVideoId(id);
    }

    @JsonIgnore
    @NonNull public ProviderId getProviderId()
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
