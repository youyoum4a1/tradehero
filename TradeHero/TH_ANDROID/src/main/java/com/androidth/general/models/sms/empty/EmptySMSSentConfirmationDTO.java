package com.androidth.general.models.sms.empty;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.androidth.general.models.sms.SMSId;
import com.androidth.general.models.sms.SMSSentConfirmationDTO;

public class EmptySMSSentConfirmationDTO implements SMSSentConfirmationDTO
{
    @NonNull private final String to;
    @NonNull private final String message;
    @StringRes private final int statusRes;

    public EmptySMSSentConfirmationDTO(@NonNull String to, @NonNull String message, @StringRes int statusRes)
    {
        this.to = to;
        this.message = message;
        this.statusRes = statusRes;
    }

    @NonNull @Override public SMSId getSMSId()
    {
        return new SMSId()
        {
        };
    }

    @NonNull @Override public String getTo()
    {
        return to;
    }

    @NonNull @Override public String getMessageBody()
    {
        return message;
    }

    @Override public int getStatusStringRes()
    {
        return statusRes;
    }

    @Override public boolean isFinalStatus()
    {
        return false;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }
}
