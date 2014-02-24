package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
public interface BillingPurchaseFetcher<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
{
    int getRequestCode();
    OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> getFetchListener();
    void setPurchaseFetchedListener(OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> fetchListener);
    void fetchPurchases(int requestCode);

    public static interface OnPurchaseFetchedListener<
            ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
            ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
            BillingExceptionType extends BillingException>
    {
        void onFetchedPurchases(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases);
        void onFetchPurchasesFailed(int requestCode, BillingExceptionType exception);
    }
}
