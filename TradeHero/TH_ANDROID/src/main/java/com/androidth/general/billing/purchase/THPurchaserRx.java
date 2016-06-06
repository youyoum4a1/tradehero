package com.androidth.general.billing.purchase;

import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.purchase.BillingPurchaserRx;
import com.androidth.general.billing.THOrderId;
import com.androidth.general.billing.THProductPurchase;
import com.androidth.general.billing.THPurchaseOrder;

public interface THPurchaserRx<
        ProductIdentifierType extends ProductIdentifier,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BillingPurchaserRx<
        ProductIdentifierType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType>
{
}
