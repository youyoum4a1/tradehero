package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
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
    protected Map<Integer /*requestCode*/, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType>> parentProductIdentifierFetchedListeners;

    public BaseProductIdentifierFetcherHolder()
    {
        super();
        parentProductIdentifierFetchedListeners = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return !parentProductIdentifierFetchedListeners.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        parentProductIdentifierFetchedListeners.remove(requestCode);
    }

    @Override public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType> getProductIdentifierFetchedListener(int requestCode)
    {
        return parentProductIdentifierFetchedListeners.get(requestCode);
    }

    @Override public void registerProductIdentifierFetchedListener(int requestCode, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierType,
            BillingExceptionType> productIdentifierFetchedListener)
    {
        parentProductIdentifierFetchedListeners.put(requestCode, productIdentifierFetchedListener);
    }

    protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> createProductIdentifierFetchedListener()
    {
        return new ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType>()
        {
            @Override public void onFetchedProductIdentifiers(int requestCode, Map<String, List<ProductIdentifierType>> availableSkus)
            {
                notifyProductIdentifierFetchedSuccess(requestCode, availableSkus);
            }

            @Override public void onFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
            {
                notifyProductIdentifierFetchedFailed(requestCode, exception);
            }
        };
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
        parentProductIdentifierFetchedListeners.clear();
    }
}
