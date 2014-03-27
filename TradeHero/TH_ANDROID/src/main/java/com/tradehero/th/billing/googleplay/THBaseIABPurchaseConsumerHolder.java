package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABSKU;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABPurchaseConsumerHolder
    extends BaseIABPurchaseConsumerHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THBaseIABPurchaseConsumer>
    implements THIABPurchaseConsumerHolder
{
    public THBaseIABPurchaseConsumerHolder()
    {
        super();
    }

    @Override protected THBaseIABPurchaseConsumer createPurchaseConsumer()
    {
        return new THBaseIABPurchaseConsumer();
    }
}
