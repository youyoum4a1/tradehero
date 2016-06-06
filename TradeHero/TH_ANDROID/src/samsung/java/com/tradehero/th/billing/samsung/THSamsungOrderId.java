package com.androidth.general.billing.samsung;

import com.androidth.general.common.billing.samsung.SamsungOrderId;
import com.androidth.general.billing.THOrderId;

public class THSamsungOrderId
        extends SamsungOrderId
        implements THOrderId
{
    public THSamsungOrderId(String purchaseId)
    {
        super(purchaseId);
    }
}
