package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABActorPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActorPurchaseFetcher extends IABActorPurchaseFetcher<
        IABSKU,
        THIABOrderId,
        BaseIABPurchase,
        IABPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKU,
                THIABOrderId,
                BaseIABPurchase>>
{
}
