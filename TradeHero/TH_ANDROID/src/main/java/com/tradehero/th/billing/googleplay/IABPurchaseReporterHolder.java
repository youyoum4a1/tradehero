package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.PurchaseReporterHolder;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface IABPurchaseReporterHolder<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        OnPurchaseReportedListenerType extends PurchaseReporter.OnPurchaseReportedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>,
        IABExceptionType extends IABException>
    extends PurchaseReporterHolder<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            OnPurchaseReportedListenerType,
            IABExceptionType>
{
}
