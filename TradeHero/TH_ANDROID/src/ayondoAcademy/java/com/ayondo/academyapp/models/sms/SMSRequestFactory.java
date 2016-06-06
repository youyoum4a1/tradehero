package com.ayondo.academyapp.models.sms;

import android.support.annotation.NonNull;
import com.androidth.general.models.sms.twilio.TwilioSMSRequest;

public class SMSRequestFactory
{
    @NonNull public static SMSRequest create(
            @NonNull String toNumber,
            @NonNull String messageBody)
    {
        return new TwilioSMSRequest(toNumber, messageBody);
    }
}
