package com.tradehero.th.models.sms.twilio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class TwilioAccountSid
{
    @NonNull public final String id;

    @JsonCreator public TwilioAccountSid(@NonNull String id)
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
