package com.androidth.general.billing.googleplay.consumer;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.consume.IABPurchaseConsumerRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;

public interface THIABPurchaseConsumerRx
        extends IABPurchaseConsumerRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
