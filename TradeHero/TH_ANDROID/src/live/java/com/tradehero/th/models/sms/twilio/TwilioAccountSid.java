package com.tradehero.th.models.sms.twilio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;

public class TwilioAccountSid
{
    @NonNull public final String id;

    @JsonCreator public TwilioAccountSid(@NonNull String id)
    {
        this.id = id;
    }
}
