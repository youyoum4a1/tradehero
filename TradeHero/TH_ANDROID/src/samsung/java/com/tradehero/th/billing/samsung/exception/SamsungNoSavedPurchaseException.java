package com.tradehero.th.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.THSamsungPurchaseIncomplete;

public class SamsungNoSavedPurchaseException extends SamsungException
{
    @NonNull public final THSamsungPurchaseIncomplete incomplete;

    //<editor-fold desc="Constructors">
    public SamsungNoSavedPurchaseException(@NonNull THSamsungPurchaseIncomplete incomplete)
    {
        this.incomplete = incomplete;
    }

    public SamsungNoSavedPurchaseException(String message, @NonNull THSamsungPurchaseIncomplete incomplete)
    {
        super(message);
        this.incomplete = incomplete;
    }
    //</editor-fold>
}
