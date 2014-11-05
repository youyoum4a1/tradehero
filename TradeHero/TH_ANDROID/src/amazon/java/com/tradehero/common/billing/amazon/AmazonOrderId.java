package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.OrderId;
import android.support.annotation.NonNull;

public class AmazonOrderId implements OrderId
{
    @NonNull public final Receipt receipt;

    //<editor-fold desc="Constructors">
    public AmazonOrderId(@NonNull Receipt receipt)
    {
        this.receipt = receipt;
    }
    //</editor-fold>
}
