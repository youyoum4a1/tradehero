package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABPurchaseFetcherHolder
    extends BaseIABPurchaseFetcherHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THBaseIABPurchaseFetcher>
    implements THIABPurchaseFetcherHolder
{
    public THBaseIABPurchaseFetcherHolder()
    {
        super();
    }

    @Override protected THBaseIABPurchaseFetcher createPurchaseFetcher()
    {
        return new THBaseIABPurchaseFetcher();
    }
}
