package com.tradehero.th.models.sms;

import android.support.annotation.NonNull;

public interface SMSSentConfirmationDTO
{
    @NonNull String getTo();
    @NonNull String getMessageBody();
}
