package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABSKU;

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
