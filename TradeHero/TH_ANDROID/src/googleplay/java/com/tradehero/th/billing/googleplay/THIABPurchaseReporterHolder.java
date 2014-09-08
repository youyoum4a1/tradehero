package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THPurchaseReporterHolder;

public interface THIABPurchaseReporterHolder extends
        THPurchaseReporterHolder<
                        IABSKU,
                        THIABOrderId,
                        THIABPurchase,
                        IABException>
{
}
