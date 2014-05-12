package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;


abstract public class BaseBillingInventoryFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
    implements BillingInventoryFetcherHolder<
        ProductIdentifierType,
        ProductDetailType,
        BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>> parentInventoryFetchedListeners;

    public BaseBillingInventoryFetcherHolder()
    {
        super();
        parentInventoryFetchedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return !parentInventoryFetchedListeners.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        parentInventoryFetchedListeners.remove(requestCode);
    }

    @Override public BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener(int requestCode)
    {
        return parentInventoryFetchedListeners.get(requestCode);
    }

    /**
     * @param requestCode
     * @param inventoryFetchedListener
     */
    @Override public void registerInventoryFetchedListener(int requestCode, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener)
    {
        parentInventoryFetchedListeners.put(requestCode, inventoryFetchedListener);
    }

    protected BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> createInventoryFetchedListener()
    {
        return new BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
            {
                notifyInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
            {
                notifyInventoryFetchFailed(requestCode, productIdentifiers, exception);
            }
        };
    }

    protected void notifyInventoryFetchedSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> parentFetchedListener = getInventoryFetchedListener(requestCode);
        if (parentFetchedListener != null)
        {
            Timber.d("Notify listener");
            parentFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }
        else
        {
            Timber.d("Listener null");
        }
    }

    protected void notifyInventoryFetchFailed(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> parentFetchedListener = getInventoryFetchedListener(requestCode);
        if (parentFetchedListener != null)
        {
            parentFetchedListener.onInventoryFetchFail(requestCode, productIdentifiers, exception);
        }
    }

    @Override public void onDestroy()
    {
        parentInventoryFetchedListeners.clear();
    }
}
