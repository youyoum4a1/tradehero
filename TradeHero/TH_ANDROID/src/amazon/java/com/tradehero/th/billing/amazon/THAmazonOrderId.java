package com.ayondo.academy.billing.amazon;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.ayondo.academy.billing.THOrderId;

public class THAmazonOrderId extends AmazonOrderId
    implements THOrderId
{
    //<editor-fold desc="Constructors">
    public THAmazonOrderId(@NonNull Receipt receipt)
    {
        super(receipt);
    }
    //</editor-fold>
}
