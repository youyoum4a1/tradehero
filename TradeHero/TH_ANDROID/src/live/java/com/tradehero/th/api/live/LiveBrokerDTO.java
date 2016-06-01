package com.ayondo.academy.api.live;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class LiveBrokerDTO implements DTO
{
    @NonNull public final LiveBrokerId id;
    @NonNull public final String name;

    public LiveBrokerDTO(
            @JsonProperty("id") @NonNull LiveBrokerId id,
            @JsonProperty("name") @NonNull String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override public int hashCode()
    {
        return id.hashCode() ^ name.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        if (o == null) return false;
        if (o == this) return true;
        return o instanceof LiveBrokerDTO
                && id.equals(((LiveBrokerDTO) o).id)
                && name.equals(((LiveBrokerDTO) o).name);
    }
}
