package com.tradehero.th.billing.googleplay.purchasefetch;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.purchasefetch.IABPurchaseFetcherHolderRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.purchasefetch.THPurchaseFetcherHolderRx;

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
