package com.tradehero.th.billing.googleplay.purchasefetch;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.purchasefetch.IABPurchaseFetcherRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.purchasefetch.THPurchaseFetcherRx;

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
