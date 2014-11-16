package com.tradehero.th.billing.purchase;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.purchase.BillingPurchaserHolderRx;
import com.tradehero.th.billing.THOrderId;
import com.tradehero.th.billing.THProductPurchase;
import com.tradehero.th.billing.THPurchaseOrder;

public interface THPurchaserHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BillingPurchaserHolderRx<
        ProductIdentifierType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType>
{
}
