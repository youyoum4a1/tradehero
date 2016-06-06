package com.androidth.general.models.sms.twilio;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwilioErrorMessage
{
    public final int code;
    public final String message;
    public final String moreInfo;
    public final int status;

    public TwilioErrorMessage(
            @JsonProperty("code") int code,
            @JsonProperty("message") String message,
            @JsonProperty("more_info") String moreInfo,
            @JsonProperty("status") int status)
    {
        this.code = code;
        this.message = message;
        this.moreInfo = moreInfo;
        this.status = status;
    }

    @Override public String toString()
    {
        return message + '\n' + moreInfo;
    }
}
