package com.androidth.general.models.sms;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.androidth.general.common.persistence.DTO;

public interface SMSSentConfirmationDTO extends DTO
{
    @NonNull SMSId getSMSId();
    @NonNull String getTo();
    @NonNull String getMessageBody();
    @StringRes int getStatusStringRes();
    boolean isFinalStatus();
}
