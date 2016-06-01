package com.ayondo.academy.billing.googleplay.consumer;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.consume.IABPurchaseConsumerHolderRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;

public interface THIABPurchaseConsumerHolderRx
        extends IABPurchaseConsumerHolderRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
