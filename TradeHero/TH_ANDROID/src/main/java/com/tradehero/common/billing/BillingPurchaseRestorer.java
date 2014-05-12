package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.List;


public interface BillingPurchaseRestorer<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
{
    public static interface OnPurchaseRestorerListener<
            ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException>
    {
        void onPurchaseRestored(int requestCode, List<ProductPurchaseType> restoredPurchases,
                List<ProductPurchaseType> failedRestorePurchases, List<BillingExceptionType> failExceptions);
    }
}
