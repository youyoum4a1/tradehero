package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;

public class CompetitionPreSeasonDTO implements DTO
{
    public int providerId;
    public String title;
    public String description;
    public String headline;
    public String content;
    public String tncUrl;
    public String prizeImageUrl;

    public CompetitionPreSeasonDTO()
    {
        super();
    }
}
