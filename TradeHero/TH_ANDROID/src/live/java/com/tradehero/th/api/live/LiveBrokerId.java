package com.ayondo.academy.api.live;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.common.persistence.DTOKey;

public class LiveBrokerId implements DTOKey
{
    @NonNull public final Integer key;

    @JsonCreator public LiveBrokerId(int key)
    {
        this.key = key;
    }

    @Override public int hashCode()
    {
        return key.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof LiveBrokerId && ((LiveBrokerId) other).key.equals(key);
    }

    @JsonValue public int getKey()
    {
        return key;
    }
}
