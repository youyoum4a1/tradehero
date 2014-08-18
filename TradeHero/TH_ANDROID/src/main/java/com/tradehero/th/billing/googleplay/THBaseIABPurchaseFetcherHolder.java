package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;

class THBaseIABPurchaseFetcherHolder
    extends BaseIABPurchaseFetcherHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THBaseIABPurchaseFetcher>
    implements THIABPurchaseFetcherHolder
{
    //<editor-fold desc="Constructors">
    public THBaseIABPurchaseFetcherHolder()
    {
        super();
    }
    //</editor-fold>

    @Override protected THBaseIABPurchaseFetcher createPurchaseFetcher()
    {
        return new THBaseIABPurchaseFetcher();
    }
}
