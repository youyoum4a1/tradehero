package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THPurchaseFetcher;

public interface THIABPurchaseFetcher
        extends
        IABPurchaseFetcher<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABException>,
        THPurchaseFetcher<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABException>
{
}
