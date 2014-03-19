package com.tradehero.common.billing;

import android.content.Intent;
import android.content.res.Resources;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BillingRequest;

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
    extends
        BillingAvailableTesterHolder<
                BillingExceptionType>,
        ProductIdentifierFetcherHolder<
                ProductIdentifierType,
                BillingExceptionType>,
        BillingInventoryFetcherHolder<
                ProductIdentifierType,
                ProductDetailType,
                BillingExceptionType>,
        BillingPurchaseFetcherHolder<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingPurchaserHolder<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>
{
    String getBillingHolderName(Resources resources);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    int getUnusedRequestCode();

    void registerListeners(int requestCode, BillingRequestType billingRequest);
    boolean run(int requestCode, BillingRequestType billingRequest);

    void unregisterBillingAvailableListener(int requestCode);
    void unregisterProductIdentifierFetchedListener(int requestCode);
    void unregisterInventoryFetchedListener(int requestCode);
    void unregisterPurchaseFetchedListener(int requestCode);
    void unregisterPurchaseFinishedListener(int requestCode);

    void onDestroy();
}
