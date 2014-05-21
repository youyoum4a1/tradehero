package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseProductIdentifierFetcherHolder<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
    implements ProductIdentifierFetcherHolder<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType>
{
    protected Map<Integer /*requestCode*/, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
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
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> getProductIdentifierFetchedListener(int requestCode)
    {
        return parentProductIdentifierFetchedListeners.get(requestCode);
    }

    @Override public void registerProductIdentifierFetchedListener(int requestCode, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType,
            BillingExceptionType> productIdentifierFetchedListener)
    {
        parentProductIdentifierFetchedListeners.put(requestCode, productIdentifierFetchedListener);
    }

    protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType,
            ProductIdentifierType,
            ProductIdentifierListType, BillingExceptionType> createProductIdentifierFetchedListener()
    {
        return new ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType, BillingExceptionType>()
        {
            @Override public void onFetchedProductIdentifiers(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableSkus)
            {
                notifyProductIdentifierFetchedSuccess(requestCode, availableSkus);
            }

            @Override public void onFetchProductIdentifiersFailed(int requestCode, BillingExceptionType exception)
            {
                notifyProductIdentifierFetchedFailed(requestCode, exception);
            }
        };
    }

    protected void notifyProductIdentifierFetchedSuccess(int requestCode, Map<ProductIdentifierListKeyType, ProductIdentifierListType> availableSkus)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                BillingExceptionType> fetchedListener = getProductIdentifierFetchedListener(requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchedProductIdentifiers(requestCode, availableSkus);
        }
    }

    protected void notifyProductIdentifierFetchedFailed(int requestCode, BillingExceptionType exception)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
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
