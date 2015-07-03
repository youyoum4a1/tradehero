package com.tradehero.th.models.sms;

import android.support.annotation.NonNull;
import com.tradehero.th.models.sms.twilio.TwilioSMSRequest;

public class SMSRequestFactory
{
    @NonNull public static SMSRequest create(
            @NonNull String toNumber,
            @NonNull String messageBody)
    {
        return new TwilioSMSRequest(toNumber, messageBody);
    }
}
