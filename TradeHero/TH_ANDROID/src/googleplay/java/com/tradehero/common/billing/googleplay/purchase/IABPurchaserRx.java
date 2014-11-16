package com.tradehero.common.billing.googleplay.purchase;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.purchase.BillingPurchaserRx;
import com.tradehero.th.activities.ActivityResultRequester;

public interface IABPurchaserRx<
        IABSKUType extends IABSKU,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
    extends BillingPurchaserRx<
            IABSKUType,
            IABPurchaseOrderType,
            IABOrderIdType,
            IABPurchaseType>,
        ActivityResultRequester
{
}
