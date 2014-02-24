package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseBillingInventoryFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        InventoryFetchedListenerType extends BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingInventoryFetcherHolder<
        ProductIdentifierType,
        ProductDetailType,
        InventoryFetchedListenerType,
        BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>>
            inventoryFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<InventoryFetchedListenerType>>parentInventoryFetchedListeners;

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

    @Override public InventoryFetchedListenerType getInventoryFetchedListener(int requestCode)
    {
        WeakReference<InventoryFetchedListenerType> weakFetchedListener = parentInventoryFetchedListeners.get(requestCode);
        if (weakFetchedListener == null)
        {
            return null;
        }
        return weakFetchedListener.get();
    }

    /**
     * The listener needs to be strong referenced elsewhere
     * @param requestCode
     * @param inventoryFetchedListener
     */
    @Override public void registerInventoryFetchedListener(int requestCode, InventoryFetchedListenerType inventoryFetchedListener)
    {
        parentInventoryFetchedListeners.put(requestCode, new WeakReference<>(inventoryFetchedListener));
    }

    protected void notifyInventoryFetchedSuccess(int requestCode, List<ProductIdentifierType> productIdentifiers, Map<ProductIdentifierType, ProductDetailType> inventory)
    {
        InventoryFetchedListenerType parentFetchedListener = getInventoryFetchedListener(requestCode);
        if (parentFetchedListener != null)
        {
            parentFetchedListener.onInventoryFetchSuccess(requestCode, productIdentifiers, inventory);
        }
    }

    protected void notifyInventoryFetchFailed(int requestCode, List<ProductIdentifierType> productIdentifiers, BillingExceptionType exception)
    {
        InventoryFetchedListenerType parentFetchedListener = getInventoryFetchedListener(requestCode);
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
