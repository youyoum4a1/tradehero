package com.androidth.general.billing.googleplay.purchasefetch;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.purchasefetch.IABPurchaseFetcherHolderRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.purchasefetch.THPurchaseFetcherHolderRx;

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
