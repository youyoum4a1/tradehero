package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.key.ProviderDisplayCellId;

public class ProviderDisplayCellDTO implements DTO
{
    int id;
    int providerId;
    String title;
    String subtitle;
    String imageUrl;
    String redirectUrl;

    @Override public String toString()
    {
        return "ProviderDisplayCellDTO{"+
                "id=" + id +
                ", providerId='" + providerId + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }

    public ProviderDisplayCellId getProviderDisplayCellId()
    {
        return new ProviderDisplayCellId(id);
    }
}
