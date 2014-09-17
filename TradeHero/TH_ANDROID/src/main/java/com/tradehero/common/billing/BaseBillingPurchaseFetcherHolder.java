package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @NotNull protected final Map<Integer /*requestCode*/, BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>> parentPurchaseFetchedListeners;

    //<editor-fold desc="Constructors">
    public BaseBillingPurchaseFetcherHolder()
    {
        super();
        parentPurchaseFetchedListeners = new HashMap<>();
    }
    //</editor-fold>

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !parentPurchaseFetchedListeners.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        parentPurchaseFetchedListeners.remove(requestCode);
    }

    @Override @Nullable public BillingPurchaseFetcher.OnPurchaseFetchedListener<
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
    @Override public void registerPurchaseFetchedListener(
            int requestCode,
            @Nullable BillingPurchaseFetcher.OnPurchaseFetchedListener<
                    ProductIdentifierType,
                    OrderIdType,
                    ProductPurchaseType,
                    BillingExceptionType> purchaseFetchedListener)
    {
        parentPurchaseFetchedListeners.put(requestCode, purchaseFetchedListener);
    }

    @NotNull protected BillingPurchaseFetcher.OnPurchaseFetchedListener<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>
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
