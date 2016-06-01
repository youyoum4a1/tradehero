package com.ayondo.academy.billing.googleplay.purchasefetch;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.purchasefetch.IABPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import com.ayondo.academy.billing.purchasefetch.THPurchaseFetcherHolderRx;

public interface THIABPurchaseFetcherHolderRx
        extends
        IABPurchaseFetcherHolderRx<
                IABSKU,
                THIABOrderId,
                THIABPurchase>,
        THPurchaseFetcherHolderRx<
                IABSKU,
                THIABOrderId,
                THIABPurchase>
{
}
