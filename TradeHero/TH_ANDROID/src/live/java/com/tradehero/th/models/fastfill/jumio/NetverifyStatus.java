package com.tradehero.th.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.tradehero.th.models.fastfill.DocumentCheckStatus;

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
