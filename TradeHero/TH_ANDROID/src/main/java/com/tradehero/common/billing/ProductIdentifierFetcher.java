package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:15 PM To change this template use File | Settings | File Templates. */
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
    Map<ProductIdentifierListKeyType, ProductIdentifierListType> fetchProductIdentifiersSync();

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
