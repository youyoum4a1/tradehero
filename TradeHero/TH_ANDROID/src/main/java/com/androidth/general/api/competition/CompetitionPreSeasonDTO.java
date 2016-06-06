package com.androidth.general.api.competition;

import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;

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

    @Nullable public String getNonEmptyPrizeImageUrl()
    {
        return prizeImageUrl == null || prizeImageUrl.isEmpty() ? null : prizeImageUrl;
    }
}
