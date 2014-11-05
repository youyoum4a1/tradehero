package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.th.billing.THOrderId;
import android.support.annotation.NonNull;

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
