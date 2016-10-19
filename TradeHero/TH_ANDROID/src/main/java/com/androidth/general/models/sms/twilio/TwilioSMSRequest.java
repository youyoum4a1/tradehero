package com.androidth.general.models.sms.twilio;

import android.support.annotation.NonNull;
import com.androidth.general.models.sms.SMSRequest;

public class TwilioSMSRequest implements SMSRequest
{
    @NonNull private final String fromNumberOrName;
    @NonNull private final String toNumber;
    @NonNull private final String messageBody;

    public TwilioSMSRequest(
            @NonNull String toNumber,
            @NonNull String messageBody)
    {
        this(TwilioConstants.API_SENDER, toNumber, messageBody);
    }

    public TwilioSMSRequest(
            @NonNull String fromNumberOrName,
            @NonNull String toNumber,
            @NonNull String messageBody)
    {
        this.fromNumberOrName = fromNumberOrName;
        this.toNumber = toNumber;
        this.messageBody = messageBody;
    }

    @NonNull public String getFromNumberOrName()
    {
        return fromNumberOrName;
    }

    @Override @NonNull public String getToNumber()
    {
        return toNumber;
    }

    @Override @NonNull public String getMessageBody()
    {
        return messageBody;
    }
}
