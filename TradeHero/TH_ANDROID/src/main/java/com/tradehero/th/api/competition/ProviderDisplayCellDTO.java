package com.tradehero.th.api.competition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.key.ProviderDisplayCellId;

public class ProviderDisplayCellDTO implements DTO
{
    public int id;
    public int providerId;
    public String title;
    public String subtitle;
    public String imageUrl;
    public String redirectUrl;

    @JsonIgnore
    public ProviderDisplayCellId getProviderDisplayCellId()
    {
        return new ProviderDisplayCellId(id);
    }

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
}
