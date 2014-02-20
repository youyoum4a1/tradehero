package com.tradehero.th.fragments.competition.zone.dto;

import com.tradehero.common.persistence.DTO;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionZoneDTO implements DTO
{
    public static final String TAG = CompetitionZoneDTO.class.getSimpleName();

    public final String title;
    public final String description;

    public CompetitionZoneDTO(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    @Override public int hashCode()
    {
        return (title == null ? new Integer(0) : title).hashCode() ^
                (description == null ? new Integer(0) : description).hashCode();
    }

    @Override public String toString()
    {
        return "CompetitionZoneDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
