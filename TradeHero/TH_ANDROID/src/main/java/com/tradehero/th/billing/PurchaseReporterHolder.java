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
    void forgetRequestCode(int requestCode);

    OnPurchaseReportedListenerType getPurchaseReportListener(int requestCode);
    int registerPurchaseReportedListener(OnPurchaseReportedListenerType purchaseReportedListener);
    void launchReportSequence(int requestCode, ProductPurchaseType purchase);
    UserProfileDTOType launchReportSequenceSync(ProductPurchaseType purchase) throws BillingException;
}
