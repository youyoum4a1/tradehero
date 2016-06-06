package com.androidth.general.common.billing.purchasefetch;

import com.androidth.general.common.billing.BaseRequestCodeActor;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;

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
