package com.ayondo.academy.billing.purchase;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.purchase.BillingPurchaserRx;
import com.ayondo.academy.billing.THOrderId;
import com.ayondo.academy.billing.THProductPurchase;
import com.ayondo.academy.billing.THPurchaseOrder;

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
