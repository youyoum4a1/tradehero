package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface THPurchaseReporterHolder<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType> getPurchaseReportedListener(int requestCode);
    void registerPurchaseReportedListener(int requestCode, THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType> purchaseReportedListener);
    void launchReportSequence(int requestCode, THProductPurchaseType purchase);
    void onDestroy();
}
