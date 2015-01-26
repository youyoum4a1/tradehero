package com.tradehero.th.billing.samsung.exception;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.io.IOException;

public class SamsungSavingPurchaseException extends SamsungException
{
    @NonNull private final IOException ioException;

    //<editor-fold desc="Constructors">
    public SamsungSavingPurchaseException(@NonNull IOException ioException)
    {
        this.ioException = ioException;
    }

    public SamsungSavingPurchaseException(String message, @NonNull IOException ioException)
    {
        super(message);
        this.ioException = ioException;
    }
    //</editor-fold>
}
