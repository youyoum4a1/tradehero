package com.androidth.general.billing.googleplay.consumer;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.consume.IABPurchaseConsumerHolderRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;

public interface THIABPurchaseConsumerHolderRx
        extends IABPurchaseConsumerHolderRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
{
}
