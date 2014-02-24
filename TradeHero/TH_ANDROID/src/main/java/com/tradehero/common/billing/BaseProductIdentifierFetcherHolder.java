package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseProductIdentifierFetcherHolder<
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierFetchedListenerType extends ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                ProductIdentifierType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements ProductIdentifierFetcherHolder<ProductIdentifierType, ProductIdentifierFetchedListenerType, BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType>> productIdentifierFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<ProductIdentifierFetchedListenerType>> parentProductIdentifierFetchedListeners;

    public BaseProductIdentifierFetcherHolder()
    {
        super();
        productIdentifierFetchedListeners = new HashMap<>();
        parentProductIdentifierFetchedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return
                !productIdentifierFetchedListeners.containsKey(randomNumber) &&
                !parentProductIdentifierFetchedListeners.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        productIdentifierFetchedListeners.remove(requestCode);
        parentProductIdentifierFetchedListeners.remove(requestCode);
    }

    @Override public ProductIdentifierFetchedListenerType getProductIdentifierFetchedListener(int requestCode)
    {
        WeakReference<ProductIdentifierFetchedListenerType> weakListener = parentProductIdentifierFetchedListeners
                .get(requestCode);
        if (weakListener == null)
        {
            return null;
        }
        return weakListener.get();
    }

    @Override public void registerProductIdentifierFetchedListener(int requestCode, ProductIdentifierFetchedListenerType productIdentifierFetchedListener)
    {
        parentProductIdentifierFetchedListeners.put(requestCode, new WeakReference<>(productIdentifierFetchedListener));
    }

    protected void notifyProductIdentifierFetchedSuccess(int requestCode, Map<String, List<ProductIdentifierType>> availableSkus)
    {
        ProductIdentifierFetchedListenerType fetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchedProductIdentifiers(requestCode, availableSkus);
        }
    }

    protected void notifyProductIdentifierFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        ProductIdentifierFetchedListenerType fetchedListener = getProductIdentifierFetchedListener(
                requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchProductIdentifiersFailed(requestCode, exception);
        }
    }

    @Override public void onDestroy()
    {
        productIdentifierFetchedListeners.clear();
        parentProductIdentifierFetchedListeners.clear();
    }
}
