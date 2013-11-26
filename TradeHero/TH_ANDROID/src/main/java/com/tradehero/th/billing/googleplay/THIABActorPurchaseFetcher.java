package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABActorPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActorPurchaseFetcher extends IABActorPurchaseFetcher<
        IABSKU,
        THIABOrderId,
        SKUPurchase,
        IABPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKU,
                THIABOrderId,
                SKUPurchase>>
{
}
