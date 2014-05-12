package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface THIABPurchaseConsumerHolder extends IABPurchaseConsumerHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        IABException>
{
}
