package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THPurchaseFetcher;

/**
 * Created by xavier on 3/27/14.
 */
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
