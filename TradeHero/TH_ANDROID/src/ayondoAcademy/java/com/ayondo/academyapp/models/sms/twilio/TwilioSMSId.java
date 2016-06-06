package com.ayondo.academyapp.models.sms.twilio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.androidth.general.models.sms.SMSId;

public class TwilioSMSId implements SMSId
{
    @NonNull public final String id;

    @JsonCreator public static TwilioSMSId create(@NonNull String id)
    {
        return new TwilioSMSId(id);
    }

    public TwilioSMSId(String id)
    {
        this.id = id;
    }

    @Override public int hashCode()
    {
        return id.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        return o instanceof TwilioSMSId && ((TwilioSMSId) o).id.equals(id);
    }

    @JsonValue @NonNull String getId()
    {
        return id;
    }
}
