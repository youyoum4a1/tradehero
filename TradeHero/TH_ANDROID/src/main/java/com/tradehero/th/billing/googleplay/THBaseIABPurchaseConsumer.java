package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;

public class THBaseIABPurchaseConsumer
        extends BaseIABPurchaseConsumer<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
    implements THIABPurchaseConsumer
{
    @Inject protected THIABPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    public THBaseIABPurchaseConsumer()
    {
        super();
    }
    //</editor-fold>

    @Override protected IABPurchaseCache<IABSKU, THIABOrderId, THIABPurchase> getPurchaseCache()
    {
        return thiabPurchaseCache;
    }
}
