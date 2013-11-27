package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.billing.googleplay.IABSKUFetcher;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:58 PM To change this template use File | Settings | File Templates. */
abstract public class BaseSKUFetcher<
        ProductIdentifierType extends ProductIdentifier,
        OnSKUFetchedListenerType extends SKUFetcher.OnSKUFetchedListener<ProductIdentifierType>>
        implements SKUFetcher<ProductIdentifierType, OnSKUFetchedListenerType>
{
    public static final String TAG = BaseSKUFetcher.class.getSimpleName();

    protected int requestCode;
    protected Map<String, List<ProductIdentifierType>> availableSkus;
    protected WeakReference<OnSKUFetchedListenerType> listener = new WeakReference<>(null);

    public BaseSKUFetcher()
    {
        super();
        availableSkus = new HashMap<>();
    }

    public void dispose()
    {
        listener = null;
    }

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override public OnSKUFetchedListenerType getListener()
    {
        return listener.get();
    }

    @Override public void setListener(OnSKUFetchedListenerType listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    protected void notifyListenerFetched()
    {
        OnSKUFetchedListener<ProductIdentifierType> listenerCopy = getListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedSKUs(requestCode, Collections.unmodifiableMap(availableSkus));
        }
    }

    protected void notifyListenerFetchFailed(Exception exception)
    {
        OnSKUFetchedListener<ProductIdentifierType> listenerCopy = getListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchSKUsFailed(requestCode, exception);
        }
    }
}
