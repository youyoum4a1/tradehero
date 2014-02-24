package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface PurchaseReporterHolder<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        UserProfileDTOType extends UserProfileDTO,
        OnPurchaseReportedListenerType extends PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    OnPurchaseReportedListenerType getPurchaseReportListener(int requestCode);
    void registerPurchaseReportedListener(int requestCode, OnPurchaseReportedListenerType purchaseReportedListener);
    void unregisterPurchaseReportedListener(int requestCode);
    void launchReportSequence(int requestCode, ProductPurchaseType purchase);
    UserProfileDTO launchReportSequenceSync(ProductPurchaseType purchase) throws BillingExceptionType;
    void onDestroy();
}
