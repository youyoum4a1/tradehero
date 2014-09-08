package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.th.billing.THOrderId;
import org.jetbrains.annotations.NotNull;

public class THAmazonOrderId extends AmazonOrderId
    implements THOrderId
{
    //<editor-fold desc="Constructors">
    public THAmazonOrderId(@NotNull Receipt receipt)
    {
        super(receipt);
    }
    //</editor-fold>
}
