package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:25 PM To change this template use File | Settings | File Templates. */
public interface THIABPurchaseConsumerHolder extends IABPurchaseConsumerHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        IABException>
{
}
