package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.HashMap;
import java.util.List;
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
    protected Map<Integer /*requestCode*/, BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>> parentPurchaseFetchedListeners;

    public BaseBillingPurchaseFetcherHolder()
    {
        super();
        parentPurchaseFetchedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !parentPurchaseFetchedListeners.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
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

    protected BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>
        createPurchaseFetchedListener()
    {
        return new BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onFetchPurchasesFailed(int requestCode, BillingExceptionType exception)
            {
                notifyPurchaseFetchedFailed(requestCode, exception);
            }

            @Override public void onFetchedPurchases(int requestCode, List<ProductPurchaseType> purchases)
            {
                notifyPurchaseFetchedSuccess(requestCode, purchases);
            }
        };
    }

    protected void notifyPurchaseFetchedSuccess(int requestCode, List<ProductPurchaseType> purchases)
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
        parentPurchaseFetchedListeners.clear();
    }
}
