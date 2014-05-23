package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.common.persistence.DTO;

public class CompetitionZoneDTO implements DTO
{
    public final String title;
    public final String description;

    public CompetitionZoneDTO(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    @Override public int hashCode()
    {
        return (title == null ? Integer.valueOf(0) : title).hashCode() ^
                (description == null ? Integer.valueOf(0) : description).hashCode();
    }

    @Override public String toString()
    {
        return "CompetitionZoneDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
