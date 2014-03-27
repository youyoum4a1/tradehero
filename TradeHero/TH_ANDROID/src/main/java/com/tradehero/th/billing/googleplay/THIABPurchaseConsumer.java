package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 3/27/14.
 */
public interface THIABPurchaseConsumer
    extends IABPurchaseConsumer<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        IABException>
{
}
