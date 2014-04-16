package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:01 PM To change this template use File | Settings | File Templates. */
public interface PurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getPurchaseReporterListener();
    void setPurchaseReporterListener(final PurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> listener);
    void reportPurchase(int requestCode, ProductPurchaseType purchase);

    public static interface OnPurchaseReportedListener<
            ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException>
    {
        void onPurchaseReported(
                int requestCode,
                ProductPurchaseType reportedPurchase,
                UserProfileDTO updatedUserPortfolio);
        void onPurchaseReportFailed(
                int requestCode,
                ProductPurchaseType reportedPurchase,
                BillingExceptionType error);
    }
}
