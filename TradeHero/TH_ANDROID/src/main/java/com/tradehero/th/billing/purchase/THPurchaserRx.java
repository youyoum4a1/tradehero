package com.tradehero.th.billing.purchase;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.purchase.BillingPurchaserRx;
import com.tradehero.th.billing.THOrderId;
import com.tradehero.th.billing.THProductPurchase;
import com.tradehero.th.billing.THPurchaseOrder;

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
