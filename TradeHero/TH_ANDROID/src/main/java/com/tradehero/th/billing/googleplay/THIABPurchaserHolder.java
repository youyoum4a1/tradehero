package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface THIABPurchaserHolder
    extends IABPurchaserHolder<
            IABSKU,
            THIABPurchaseOrder,
            THIABOrderId,
            THIABPurchase,
            IABException>
{
}
