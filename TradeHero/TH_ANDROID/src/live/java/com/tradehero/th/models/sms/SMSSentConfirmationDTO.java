package com.tradehero.th.models.sms;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface SMSSentConfirmationDTO
{
    @NonNull SMSId getSMSId();
    @NonNull String getTo();
    @NonNull String getMessageBody();
    @StringRes int getStatusStringRes();
    boolean isFinalStatus();
}
