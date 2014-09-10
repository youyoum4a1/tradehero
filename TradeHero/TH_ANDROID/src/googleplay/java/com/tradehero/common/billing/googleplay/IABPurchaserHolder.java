package com.tradehero.common.billing.googleplay;

import android.content.Intent;
import com.tradehero.common.billing.BillingPurchaserHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface IABPurchaserHolder<
        IABSKUType extends IABSKU,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABExceptionType extends IABException>
    extends BillingPurchaserHolder<
                IABSKUType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABExceptionType>
{
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
