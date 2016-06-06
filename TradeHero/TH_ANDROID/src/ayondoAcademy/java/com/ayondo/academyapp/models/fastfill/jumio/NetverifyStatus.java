package com.ayondo.academyapp.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.androidth.general.models.fastfill.DocumentCheckStatus;

public enum NetverifyStatus
{
    PENDING(DocumentCheckStatus.PENDING),
    DONE(DocumentCheckStatus.DONE),
    FAILED(DocumentCheckStatus.FAILED),
    ;

    @NonNull public final DocumentCheckStatus checkStatus;

    NetverifyStatus(@NonNull DocumentCheckStatus checkStatus)
    {
        this.checkStatus = checkStatus;
    }
}
