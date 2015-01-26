package com.tradehero.th.billing.googleplay.consumer;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.consume.IABPurchaseConsumerHolderRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;

public interface THIABPurchaseConsumerHolderRx
        extends IABPurchaseConsumerHolderRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
