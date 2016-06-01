package com.ayondo.academy.billing.googleplay.consumer;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.consume.IABPurchaseConsumerRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;

public interface THIABPurchaseConsumerRx
        extends IABPurchaseConsumerRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
