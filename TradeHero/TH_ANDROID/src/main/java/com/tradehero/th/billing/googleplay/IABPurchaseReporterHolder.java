package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.PurchaseReporterHolder;

public interface IABPurchaseReporterHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
    extends PurchaseReporterHolder<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABExceptionType>
{
}
