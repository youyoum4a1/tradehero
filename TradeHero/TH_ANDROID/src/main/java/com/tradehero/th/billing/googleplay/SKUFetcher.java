package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.SKU;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:58 PM To change this template use File | Settings | File Templates. */
public class SKUFetcher
{
    public static final String TAG = SKUFetcher.class.getSimpleName();

    private Map<String, List<SKU>> availableSkus;
    private WeakReference<SKUFetcherListener> listener = new WeakReference<>(null);

    public SKUFetcher()
    {
        super();
        availableSkus = new HashMap<>();

        // TODO hard-coded while there is nothing coming from the server.
        List<SKU> inAppSkus = new ArrayList<>();
        inAppSkus.add(new SKU("com.myhero.th.extracash.t0"));
        inAppSkus.add(new SKU("com.myhero.th.extracash.t1"));
        inAppSkus.add(new SKU("com.myhero.th.extracash.t2"));
        availableSkus.put(Constants.ITEM_TYPE_INAPP, inAppSkus);

        availableSkus.put(Constants.ITEM_TYPE_SUBS, new ArrayList<SKU>());
    }

    public void dispose()
    {
        listener = null;
    }

    public void fetchSkus()
    {
        notifyListenerFetched();
    }

    public Map<String, List<SKU>> getAvailableSkus()
    {
        // TODO find out whether the lists are modifiable
        return Collections.unmodifiableMap(availableSkus);
    }

    public List<SKU> getAvailableInAppSkus()
    {
        return getAvailableSkusOfType(Constants.ITEM_TYPE_INAPP);
    }

    public List<SKU> getAvailableSubscriptionSkus()
    {
        return getAvailableSkusOfType(Constants.ITEM_TYPE_SUBS);
    }

    private List<SKU> getAvailableSkusOfType(String itemType)
    {
        return Collections.unmodifiableList(availableSkus.get(itemType));
    }

    public SKUFetcherListener getListener()
    {
        return listener.get();
    }

    public void setListener(SKUFetcherListener listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    protected void notifyListenerFetched()
    {
        SKUFetcherListener listenerCopy = getListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedSKUs(this, Collections.unmodifiableMap(availableSkus));
        }
    }

    protected void notifyListenerFetchFailed(Exception exception)
    {
        SKUFetcherListener listenerCopy = getListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchSKUsFailed(this, exception);
        }
    }

    public static interface SKUFetcherListener
    {
        void onFetchedSKUs(SKUFetcher fetcher, Map<String, List<SKU>> availableSkus);
        void onFetchSKUsFailed(SKUFetcher fetcher, Exception exception); // TODO decide if we create specific Exception
    }
}
