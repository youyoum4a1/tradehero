package com.tradehero.common.billing;

import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/22/13 Time: 5:11 PM To change this template use File | Settings | File Templates. */
public interface InventoryFetcher<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetails<ProductIdentifierType>,
        ExceptionType extends Exception>
{
    List<ProductIdentifierType> getProductIdentifiers();
    void setProductIdentifiers(List<ProductIdentifierType> productIdentifiers);
    OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, ExceptionType> getInventoryFetchedListener();
    void setInventoryFetchedListener(OnInventoryFetchedListener<ProductIdentifierType, ProductDetailsType, ExceptionType> onInventoryFetchedListener);
    void fetchInventory(int requestCode);
    int getRequestCode();

    public static interface OnInventoryFetchedListener<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailsType extends ProductDetails<ProductIdentifierType>,
            ExceptionType extends Exception>
    {
        void onInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailsType> inventory);
        void onInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, ExceptionType exception);
    }
}
