package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:58 PM To change this template use File | Settings | File Templates. */
abstract public class BaseProductIdentifierFetcher<
        ProductIdentifierType extends ProductIdentifier,
        BillingExceptionType extends BillingException>
        implements ProductIdentifierFetcher<ProductIdentifierType, BillingExceptionType>
{
    public static final String TAG = BaseProductIdentifierFetcher.class.getSimpleName();

    protected int requestCode;
    protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> productIdentifierFetchedListener;

    public BaseProductIdentifierFetcher()
    {
        super();
    }

    @Override public void fetchProductIdentifiers(int requestCode)
    {
        this.requestCode = requestCode;
    }

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> getProductIdentifierListener()
    {
        return productIdentifierFetchedListener;
    }

    @Override public void setProductIdentifierListener(
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> listener)
    {
        this.productIdentifierFetchedListener = listener;
    }

    protected void notifyListenerFetched(Map<String, List<ProductIdentifierType>> productIdentifiers)
    {
        OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedProductIdentifiers(requestCode, productIdentifiers);
        }
    }

    protected void notifyListenerFetchFailed(BillingExceptionType exception)
    {
        OnProductIdentifierFetchedListener<ProductIdentifierType, BillingExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchProductIdentifiersFailed(requestCode, exception);
        }
    }
}
