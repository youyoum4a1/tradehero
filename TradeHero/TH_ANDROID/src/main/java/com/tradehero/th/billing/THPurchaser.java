package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;

public interface THPurchaser<
        ProductIdentifierType extends ProductIdentifier,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
        extends BillingPurchaser<
        ProductIdentifierType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType,
        BillingExceptionType>
{
}
