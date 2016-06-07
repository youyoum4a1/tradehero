package com.androidth.general.common.billing.googleplay.purchase;

import com.androidth.general.common.activities.ActivityResultRequester;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABPurchaseOrder;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.purchase.BillingPurchaserHolderRx;

public interface IABPurchaserHolderRx<
        IABSKUType extends IABSKU,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
    extends BillingPurchaserHolderRx<
                    IABSKUType,
                    IABPurchaseOrderType,
                    IABOrderIdType,
                    IABPurchaseType>,
        ActivityResultRequester
{
}
