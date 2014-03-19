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
        BillingExceptionType extends BillingException>
    implements ProductIdentifierFetcherHolder<ProductIdentifierType, BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType>> productIdentifierFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType>>> parentProductIdentifierFetchedListeners;

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

    @Override public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType> getProductIdentifierFetchedListener(int requestCode)
    {
        WeakReference<ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                ProductIdentifierType,
                BillingExceptionType>> weakListener = parentProductIdentifierFetchedListeners
                .get(requestCode);
        if (weakListener == null)
        {
            return null;
        }
        return weakListener.get();
    }

    @Override public void registerProductIdentifierFetchedListener(int requestCode, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType> productIdentifierFetchedListener)
    {
        parentProductIdentifierFetchedListeners.put(requestCode, new WeakReference<>(productIdentifierFetchedListener));
    }

    protected void notifyProductIdentifierFetchedSuccess(int requestCode, Map<String, List<ProductIdentifierType>> availableSkus)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                ProductIdentifierType,
                BillingExceptionType> fetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchedProductIdentifiers(requestCode, availableSkus);
        }
    }

    protected void notifyProductIdentifierFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                ProductIdentifierType,
                BillingExceptionType> fetchedListener = getProductIdentifierFetchedListener(
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
