package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THPurchaseReporterHolder;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface THIABPurchaseReporterHolder extends
        THPurchaseReporterHolder<
                        IABSKU,
                        THIABOrderId,
                        THIABPurchase,
                        IABException>
{
}
