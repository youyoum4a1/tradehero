package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface THIABPurchaseFetcherHolder extends IABPurchaseFetcherHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        IABException>
{
}
