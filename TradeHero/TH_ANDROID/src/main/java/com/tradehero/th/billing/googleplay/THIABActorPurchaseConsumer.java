package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABActorPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:25 PM To change this template use File | Settings | File Templates. */
public interface THIABActorPurchaseConsumer extends IABActorPurchaseConsumer<
        IABSKU,
        THIABOrderId,
        BaseIABPurchase,
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKU,
                THIABOrderId,
                BaseIABPurchase,
                IABException>,
        IABException>
{
}
