package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/22/13 Time: 5:11 PM To change this template use File | Settings | File Templates. */
public interface BillingInventoryFetcher<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
{
    int getRequestCode();
    OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener();
    void setInventoryFetchedListener(OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> onInventoryFetchedListener);
    List<ProductIdentifierType> getProductIdentifiers();
    void setProductIdentifiers(List<ProductIdentifierType> productIdentifiers);
    void fetchInventory(int requestCode);

    public static interface OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType>
    {
        void onInventoryFetchSuccess(
                int requestCode,
                List<ProductIdentifierType> productIdentifiers,
                Map<ProductIdentifierType, ProductDetailType> inventory);
        void onInventoryFetchFail(
                int requestCode,
                List<ProductIdentifierType> productIdentifiers,
                BillingExceptionType exception);
    }
}
