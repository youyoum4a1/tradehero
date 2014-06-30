package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.Map;

public interface ProductIdentifierFetcher<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
{
    int getRequestCode();
    ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> getProductIdentifierListener();
    void setProductIdentifierListener(ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> listener);
    void fetchProductIdentifiers(int requestCode);

    public static interface OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType extends ProductIdentifierListKey,
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
            BillingExceptionType extends BillingException>
    {
        void onFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableProductIdentifiers);
        void onFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception);
    }
}
