package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumerHolder;
import com.tradehero.common.billing.googleplay.IABSKU;

class THBaseIABPurchaseConsumerHolder
    extends BaseIABPurchaseConsumerHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THBaseIABPurchaseConsumer>
    implements THIABPurchaseConsumerHolder
{
    //<editor-fold desc="Constructors">
    public THBaseIABPurchaseConsumerHolder()
    {
        super();
    }
    //</editor-fold>

    @Override protected THBaseIABPurchaseConsumer createPurchaseConsumer()
    {
        return new THBaseIABPurchaseConsumer();
    }
}
