package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingInventoryFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
{
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener(int requestCode);
    void registerInventoryFetchedListener(int requestCode, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener);
    void launchInventoryFetchSequence(int requestCode, List<ProductIdentifierType> allIds);
    @Deprecated
    boolean isInventoryReady();
    @Deprecated
    boolean hadErrorLoadingInventory();
    void onDestroy();
}
