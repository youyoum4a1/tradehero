package com.androidth.general.models.sms.nexmo;

import android.support.annotation.NonNull;

import com.androidth.general.models.sms.SMSRequest;

public class NexmoSMSRequest implements SMSRequest
{
    @NonNull
    private final String fromNumberOrName;
    @NonNull private final String toNumber;
    @NonNull private final String messageBody;
    @NonNull private final String languageCode;

    public NexmoSMSRequest(
            @NonNull String toNumber,
            @NonNull String messageBody,
            @NonNull String languageCode)
    {
        this(NexmoConstants.API_SENDER, toNumber, messageBody, languageCode);
    }

    public NexmoSMSRequest(
            @NonNull String fromNumberOrName,
            @NonNull String toNumber,
            @NonNull String messageBody,
            @NonNull String languageCode)
    {
        this.fromNumberOrName = fromNumberOrName;
        this.toNumber = toNumber;
        this.messageBody = messageBody;
        this.languageCode = languageCode;
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

    @NonNull
    public String getLanguageCode() {
        return languageCode;
    }
}
