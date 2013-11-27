package com.tradehero.th.billing;

import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:01 PM To change this template use File | Settings | File Templates. */
public interface PurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        OnPurchaseReportedListenerType extends PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                ThrowableType>,
        ThrowableType extends Throwable>
{
    OnPurchaseReportedListenerType getListener();
    void setListener(final OnPurchaseReportedListenerType listener);
    void reportPurchase(int requestCode, ProductPurchaseType purchase);
    UserProfileDTO reportPurchaseSync(ProductPurchaseType purchase);

    public static interface OnPurchaseReportedListener<
            ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            ThrowableType extends Throwable>
    {
        void onPurchaseReported(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio);
        void onPurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, ThrowableType error);
    }
}
