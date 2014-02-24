package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseBillingPurchaseFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        PurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingPurchaseFetcherHolder<
        ProductIdentifierType,
        OrderIdType,
        ProductPurchaseType,
        PurchaseFetchedListenerType,
        BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>>
            purchaseFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<PurchaseFetchedListenerType>> parentPurchaseFetchedListeners;

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

    @Override public PurchaseFetchedListenerType getPurchaseFetchedListener(int requestCode)
    {
        WeakReference<PurchaseFetchedListenerType> weakListener = parentPurchaseFetchedListeners.get(requestCode);
        if (weakListener == null)
        {
            return null;
        }
        return weakListener.get();
    }

    /**
     * The listener needs to be strongly referenced elsewhere.
     * @param requestCode
     * @param purchaseFetchedListener
     */
    @Override public void registerPurchaseFetchedListener(int requestCode, PurchaseFetchedListenerType purchaseFetchedListener)
    {
        parentPurchaseFetchedListeners.put(requestCode, new WeakReference<>(purchaseFetchedListener));
    }

    protected void notifyPurchaseFetchedSuccess(int requestCode, Map<ProductIdentifierType, ProductPurchaseType> purchases)
    {
        PurchaseFetchedListenerType parentListener = getPurchaseFetchedListener(requestCode);
        if (parentListener != null)
        {
            parentListener.onFetchedPurchases(requestCode, purchases);
        }
    }

    protected void notifyPurchaseFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        PurchaseFetchedListenerType parentListener = getPurchaseFetchedListener(requestCode);
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
