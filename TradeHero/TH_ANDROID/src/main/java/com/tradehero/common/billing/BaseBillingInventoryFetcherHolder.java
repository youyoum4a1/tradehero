package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseBillingInventoryFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
    implements BillingInventoryFetcherHolder<
        ProductIdentifierType,
        ProductDetailType,
        BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>>
            inventoryFetchedListeners;
    protected Map<Integer /*requestCode*/, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>> parentInventoryFetchedListeners;

    public BaseBillingInventoryFetcherHolder()
    {
        super();
        inventoryFetchedListeners = new HashMap<>();
        parentInventoryFetchedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return
                !inventoryFetchedListeners.containsKey(randomNumber) &&
                        !parentInventoryFetchedListeners.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        inventoryFetchedListeners.remove(requestCode);
        parentInventoryFetchedListeners.remove(requestCode);
    }

    @Override public BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> getInventoryFetchedListener(int requestCode)
    {
        return parentInventoryFetchedListeners.get(requestCode);
    }

    /**
     * The listener needs to be strong referenced elsewhere
     * @param requestCode
     * @param inventoryFetchedListener
     */
    @Override public void registerInventoryFetchedListener(int requestCode, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> inventoryFetchedListener)
    {
        parentInventoryFetchedListeners.put(requestCode, inventoryFetchedListener);
    }

    protected void notifyInventoryFetchedSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> parentFetchedListener = getInventoryFetchedListener(requestCode);
        if (parentFetchedListener != null)
        {
            parentFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
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
        inventoryFetchedListeners.clear();
        parentInventoryFetchedListeners.clear();
    }
}
