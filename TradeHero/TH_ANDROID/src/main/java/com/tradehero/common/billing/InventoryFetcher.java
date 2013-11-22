package com.tradehero.common.billing;

import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/22/13 Time: 5:11 PM To change this template use File | Settings | File Templates. */
public interface InventoryFetcher<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailsType extends ProductDetails<ProductIdentifierType>,
        ExceptionType extends Exception>
{
    public List<ProductIdentifierType> getProductIdentifiers();
    public void setProductIdentifiers(List<ProductIdentifierType> iabSKUs);
    public InventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailsType,
                ExceptionType> getInventoryFetchedListener();
    public void setInventoryFetchedListener(InventoryFetchedListener<
                ProductIdentifierType,
                ProductDetailsType,
                ExceptionType> inventoryFetchedListener);

    public static interface InventoryFetchedListener<
            ProductIdentifierType extends ProductIdentifier,
            ProductDetailsType extends ProductDetails<ProductIdentifierType>,
            ExceptionType extends Exception>
    {
        void onInventoryFetchSuccess(InventoryFetcher fetcher, Map<ProductIdentifierType, ProductDetailsType> inventory);
        void onInventoryFetchFail(InventoryFetcher fetcher, ExceptionType exception);
    }
}
