package com.ayondo.academyapp.models.sms;

import android.support.annotation.NonNull;

public interface SMSRequest
{
    @NonNull String getToNumber();

    @NonNull String getMessageBody();
}
