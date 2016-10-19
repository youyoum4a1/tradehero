package com.androidth.general.models.sms;


import android.support.annotation.NonNull;

import com.androidth.general.models.sms.nexmo.NexmoSMSRequest;

public class SMSRequestFactory
{
    @NonNull public static SMSRequest create(
            @NonNull String toNumber,
            @NonNull String messageBody,
            @NonNull String languageCode)
    {
        return new NexmoSMSRequest(toNumber, messageBody, languageCode);//implements Nexmo
    }
}
