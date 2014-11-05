package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

import android.support.annotation.Nullable;

import java.util.List;

public interface BillingInventoryFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
    extends RequestCodeHolder
{
    @Nullable BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener(int requestCode);
    void registerInventoryFetchedListener(int requestCode, @Nullable BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener);
    void launchInventoryFetchSequence(int requestCode, List<ProductIdentifierType> allIds);
}
