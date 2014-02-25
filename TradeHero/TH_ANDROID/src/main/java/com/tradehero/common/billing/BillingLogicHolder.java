package com.tradehero.common.billing;

import android.content.res.Resources;
import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    String getBillingHolderName(Resources resources);
    Boolean isBillingAvailable();
    int getUnusedRequestCode();
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    void registerBillingAvailableListener(int requestCode, OnBillingAvailableListener<BillingException> billingAvailableListener);
    void onDestroy();
}
