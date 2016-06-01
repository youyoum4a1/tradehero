package com.ayondo.academy.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungOrderId;
import com.ayondo.academy.billing.THOrderId;

public class THSamsungOrderId
        extends SamsungOrderId
        implements THOrderId
{
    public THSamsungOrderId(String purchaseId)
    {
        super(purchaseId);
    }
}
