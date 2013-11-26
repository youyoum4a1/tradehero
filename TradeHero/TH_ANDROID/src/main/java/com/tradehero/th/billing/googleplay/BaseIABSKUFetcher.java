package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABSKU;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:58 PM To change this template use File | Settings | File Templates. */
abstract public class BaseIABSKUFetcher<IABSKUType extends IABSKU>
        implements IABSKUFetcher<IABSKUType>
{
    public static final String TAG = BaseIABSKUFetcher.class.getSimpleName();

    protected int requestCode;
    protected Map<String, List<IABSKUType>> availableSkus;
    protected WeakReference<OnSKUFetchedListener<IABSKUType>> listener = new WeakReference<>(null);

    public BaseIABSKUFetcher()
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

    @Override public OnSKUFetchedListener<IABSKUType> getListener()
    {
        return listener.get();
    }

    @Override public void setListener(OnSKUFetchedListener<IABSKUType> listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    protected void notifyListenerFetched()
    {
        OnSKUFetchedListener<IABSKUType> listenerCopy = getListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedSKUs(requestCode, Collections.unmodifiableMap(availableSkus));
        }
    }

    protected void notifyListenerFetchFailed(Exception exception)
    {
        OnSKUFetchedListener<IABSKUType> listenerCopy = getListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchSKUsFailed(requestCode, exception);
        }
    }
}
