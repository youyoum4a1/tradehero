package com.androidth.general.models.sms.twilio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.androidth.general.common.persistence.DTOKey;

public class TwilioAccountSid implements DTOKey
{
    @NonNull public final String id;

    @JsonCreator public static TwilioAccountSid create(@NonNull String id)
    {
        return new TwilioAccountSid(id);
    }

    public TwilioAccountSid(@NonNull String id)
    {
        this.id = id;
    }

    @Override public int hashCode()
    {
        return id.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        return o instanceof TwilioAccountSid && ((TwilioAccountSid) o).id.equals(id);
    }

    @JsonValue @NonNull String getId()
    {
        return id;
    }
}
