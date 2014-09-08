package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.OrderId;
import org.jetbrains.annotations.NotNull;

public class AmazonOrderId implements OrderId
{
    @NotNull public final Receipt receipt;

    //<editor-fold desc="Constructors">
    public AmazonOrderId(@NotNull Receipt receipt)
    {
        this.receipt = receipt;
    }
    //</editor-fold>
}
