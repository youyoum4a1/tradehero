package com.ayondo.academy.billing.googleplay.purchasefetch;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.purchasefetch.IABPurchaseFetcherRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import com.ayondo.academy.billing.purchasefetch.THPurchaseFetcherRx;

public interface THIABPurchaseFetcherRx
        extends
        IABPurchaseFetcherRx<
                IABSKU,
                THIABOrderId,
                THIABPurchase>,
        THPurchaseFetcherRx<
                IABSKU,
                THIABOrderId,
                THIABPurchase>
{
}
