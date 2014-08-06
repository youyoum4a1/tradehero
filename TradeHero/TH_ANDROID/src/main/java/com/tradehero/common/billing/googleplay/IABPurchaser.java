package com.tradehero.common.billing.googleplay;

import android.content.Intent;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.exception.IABException;

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
    boolean handleActivityResult(int requestCode, int resultCode, Intent data);
}
