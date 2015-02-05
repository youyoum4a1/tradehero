package com.tradehero.common.billing.purchasefetch;

import com.tradehero.common.billing.BaseRequestCodeActor;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;

abstract public class BaseBillingPurchaseFetcherRx<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseRequestCodeActor
        implements BillingPurchaseFetcherRx<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType>
{
    //<editor-fold desc="Constructors">
    protected BaseBillingPurchaseFetcherRx(int requestCode)
    {
        super(requestCode);
    }
    //</editor-fold>
}
