package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingPurchaserHolder;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;

/**
 * Created by xavier on 3/28/14.
 */
public interface THPurchaserHolder<
        ProductIdentifierType extends ProductIdentifier,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
    extends BillingPurchaserHolder<
        ProductIdentifierType,
        THPurchaseOrderType,
        THOrderIdType,
        THProductPurchaseType,
        BillingExceptionType>
{
}
