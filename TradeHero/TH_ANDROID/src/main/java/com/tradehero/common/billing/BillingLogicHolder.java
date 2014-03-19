package com.tradehero.common.billing;

import android.content.Intent;
import android.content.res.Resources;
import com.tradehero.common.billing.exception.BillingException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface BillingLogicHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingRequestType extends BillingRequest<
            ProductIdentifierType,
            ProductDetailType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>,
        BillingExceptionType extends BillingException>
{
    String getBillingHolderName(Resources resources);
    Boolean isBillingAvailable();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    int getUnusedRequestCode();
    boolean isUnusedRequestCode(int requestCode);
    void forgetRequestCode(int requestCode);
    void registerListeners(int requestCode, BillingRequestType billingRequest);
    void registerBillingAvailableListener(int requestCode, OnBillingAvailableListener<BillingExceptionType> billingAvailableListener);
    void registerInventoryFetchedListener(int requestCode, BillingInventoryFetcher.OnInventoryFetchedListener<
            ProductIdentifierType,
            ProductDetailType,
            BillingExceptionType> inventoryFetchedListener);
    void registerPurchaseFetchedListener(int requestCode,
            BillingPurchaseFetcher.OnPurchaseFetchedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFetchedListener);
    void onDestroy();
}
