package com.androidth.general.billing.googleplay.purchasefetch;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.purchasefetch.IABPurchaseFetcherRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.purchasefetch.THPurchaseFetcherRx;

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
