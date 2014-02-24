package com.tradehero.common.billing;

/**
 * Created by xavier on 2/21/14.
 */
public interface BillingPurchaseRestorer<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
{

}
