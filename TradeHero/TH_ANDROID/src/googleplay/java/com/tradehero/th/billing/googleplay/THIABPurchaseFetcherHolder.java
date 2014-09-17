package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THPurchaseFetcherHolder;

public interface THIABPurchaseFetcherHolder
        extends
        IABPurchaseFetcherHolder<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABException>,
        THPurchaseFetcherHolder<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABException>
{
}
