package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABPurchaseFetcherHolder
    extends BaseIABPurchaseFetcherHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaseFetcher,
        BillingPurchaseFetcher.OnPurchaseFetchedListener<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            IABException>>
    implements THIABPurchaseFetcherHolder
{
    public THBaseIABPurchaseFetcherHolder()
    {
        super();
    }

    @Override protected THIABPurchaseFetcher createPurchaseFetcher()
    {
        return new THIABPurchaseFetcher();
    }
}
