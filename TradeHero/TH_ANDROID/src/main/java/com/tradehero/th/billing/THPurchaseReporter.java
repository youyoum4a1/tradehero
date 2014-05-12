package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.users.UserProfileDTO;

public interface THPurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
{
    int getRequestCode();
    THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> getPurchaseReporterListener();
    void setPurchaseReporterListener(final THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener);
    void reportPurchase(int requestCode, THProductPurchaseType purchase);

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
