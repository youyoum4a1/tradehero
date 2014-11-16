package com.tradehero.common.billing;

import android.support.annotation.Nullable;
import com.tradehero.common.billing.exception.BillingException;
import java.util.Map;

abstract public class BaseProductIdentifierFetcher<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        BillingExceptionType extends BillingException>
        implements ProductIdentifierFetcher<
        ProductIdentifierListKeyType,
        ProductIdentifierType,
        ProductIdentifierListType,
        BillingExceptionType>
{
    protected final int requestCode;
    @Nullable protected ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> productIdentifierFetchedListener;

    //<editor-fold desc="Constructors">
    public BaseProductIdentifierFetcher(int requestCode)
    {
        super();
        this.requestCode = requestCode;
    }
    //</editor-fold>

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override @Nullable public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> getProductIdentifierListener()
    {
        return productIdentifierFetchedListener;
    }

    @Override public void setProductIdentifierListener(
            @Nullable ProductIdentifierFetcher.OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> listener)
    {
        this.productIdentifierFetchedListener = listener;
    }

    protected void notifyListenerFetched(Map<ProductIdentifierListKeyType, ProductIdentifierListType> productIdentifiers)
    {
        OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedProductIdentifiers(requestCode, productIdentifiers);
        }
    }

    protected void notifyListenerFetchFailed(BillingExceptionType exception)
    {
        OnProductIdentifierFetchedListener<ProductIdentifierListKeyType, ProductIdentifierType, ProductIdentifierListType, BillingExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchProductIdentifiersFailed(requestCode, exception);
        }
    }
}
