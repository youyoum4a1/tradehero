package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseBillingPurchaseFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
    implements BillingPurchaseFetcherHolder<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType,
        BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>>
            purchaseFetchedListeners;
    protected Map<Integer /*requestCode*/, BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>> parentPurchaseFetchedListeners;

    public BaseBillingPurchaseFetcherHolder()
    {
        super();
        purchaseFetchedListeners = new HashMap<>();
        parentPurchaseFetchedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !purchaseFetchedListeners.containsKey(requestCode) &&
                !parentPurchaseFetchedListeners.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        purchaseFetchedListeners.remove(requestCode);
        parentPurchaseFetchedListeners.remove(requestCode);
    }

    @Override public BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> getPurchaseFetchedListener(int requestCode)
    {
        return parentPurchaseFetchedListeners.get(requestCode);
    }

    /**
     * The listener needs to be strongly referenced elsewhere.
     * @param requestCode
     * @param purchaseFetchedListener
     */
    @Override public void registerPurchaseFetchedListener(int requestCode, BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener)
    {
        parentPurchaseFetchedListeners.put(requestCode, purchaseFetchedListener);
    }

    protected void notifyPurchaseFetchedSuccess(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> parentListener = getPurchaseFetchedListener(requestCode);
        if (parentListener != null)
        {
            parentListener.onFetchedPurchases(requestCode, purchases);
        }
    }

    protected void notifyPurchaseFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType> parentListener = getPurchaseFetchedListener(requestCode);
        if (parentListener != null)
        {
            parentListener.onFetchPurchasesFailed(requestCode, exception);
        }
    }

    @Override public void onDestroy()
    {
        purchaseFetchedListeners.clear();
        parentPurchaseFetchedListeners.clear();
    }
}
