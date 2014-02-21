package com.tradehero.common.billing;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingActorInventoryFetcher<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        InventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, ExceptionType>,
        ExceptionType extends Exception>
{
    void forgetRequestCode(int requestCode);
    boolean isBillingAvailable();

    InventoryFetchedListenerType getInventoryFetchedListener(int requestCode);
    int registerInventoryFetchedListener(InventoryFetchedListenerType inventoryFetchedListener);
    void launchInventoryFetchSequence(int requestCode);
    boolean isInventoryReady();
    boolean hadErrorLoadingInventory();
}
