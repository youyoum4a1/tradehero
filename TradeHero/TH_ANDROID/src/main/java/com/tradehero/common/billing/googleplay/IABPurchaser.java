package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 3/27/14.
 */
public interface IABPurchaser<
        IABSKUType extends IABSKU,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
    extends BillingPurchaser<
        IABSKUType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType,
        IABExceptionType>
{
}
