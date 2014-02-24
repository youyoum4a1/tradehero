package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.googleplay.IABProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    boolean isBillingAvailable();
    int getUnusedRequestCode();
    void forgetRequestCode(int requestCode);
    void onDestroy();
}
