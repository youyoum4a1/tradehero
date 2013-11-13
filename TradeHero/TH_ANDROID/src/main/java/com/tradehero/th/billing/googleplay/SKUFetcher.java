package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABSKU;
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

    public static final String EXTRA_CASH_T0_KEY = "com.myhero.th.extracash.t0";
    public static final String EXTRA_CASH_T1_KEY = "com.myhero.th.extracash.t1";
    public static final String EXTRA_CASH_T2_KEY = "com.myhero.th.extracash.t2";

    public static final String CREDIT_1 = "com.myhero.th.1cc";
    public static final String CREDIT_5 = "com.myhero.th.5cc";
    public static final String CREDIT_10 = "com.myhero.th.10cc";
    public static final String CREDIT_20 = "com.myhero.th.20cc";

    public static final String RESET_PORTFOLIO_0 = "com.myhero.th.resetportfolio.0";

    private Map<String, List<IABSKU>> availableSkus;
    private WeakReference<SKUFetcherListener> listener = new WeakReference<>(null);

    public SKUFetcher()
    {
        super();
        availableSkus = new HashMap<>();

        // TODO hard-coded while there is nothing coming from the server.
        List<IABSKU> inAppIABSKUs = new ArrayList<>();
        inAppIABSKUs.add(new IABSKU(EXTRA_CASH_T0_KEY));
        inAppIABSKUs.add(new IABSKU(EXTRA_CASH_T1_KEY));
        inAppIABSKUs.add(new IABSKU(EXTRA_CASH_T2_KEY));
        inAppIABSKUs.add(new IABSKU(CREDIT_1));
        inAppIABSKUs.add(new IABSKU(CREDIT_5));
        inAppIABSKUs.add(new IABSKU(CREDIT_10));
        inAppIABSKUs.add(new IABSKU(CREDIT_20));
        inAppIABSKUs.add(new IABSKU(RESET_PORTFOLIO_0));
        availableSkus.put(Constants.ITEM_TYPE_INAPP, inAppIABSKUs);

        availableSkus.put(Constants.ITEM_TYPE_SUBS, new ArrayList<IABSKU>());
    }

    public void dispose()
    {
        listener = null;
    }

    public void fetchSkus()
    {
        notifyListenerFetched();
    }

    public Map<String, List<IABSKU>> getAvailableSkus()
    {
        // TODO find out whether the lists are modifiable
        return Collections.unmodifiableMap(availableSkus);
    }

    public List<IABSKU> getAvailableInAppSkus()
    {
        return getAvailableSkusOfType(Constants.ITEM_TYPE_INAPP);
    }

    public List<IABSKU> getAvailableSubscriptionSkus()
    {
        return getAvailableSkusOfType(Constants.ITEM_TYPE_SUBS);
    }

    private List<IABSKU> getAvailableSkusOfType(String itemType)
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
        void onFetchedSKUs(SKUFetcher fetcher, Map<String, List<IABSKU>> availableSkus);
        void onFetchSKUsFailed(SKUFetcher fetcher, Exception exception); // TODO decide if we create specific Exception
    }
}
