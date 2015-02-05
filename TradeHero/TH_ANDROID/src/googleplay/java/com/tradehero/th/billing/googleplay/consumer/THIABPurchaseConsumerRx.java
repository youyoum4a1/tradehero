package com.tradehero.th.billing.googleplay.consumer;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.consume.IABPurchaseConsumerRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;

public interface THIABPurchaseConsumerRx
        extends IABPurchaseConsumerRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
