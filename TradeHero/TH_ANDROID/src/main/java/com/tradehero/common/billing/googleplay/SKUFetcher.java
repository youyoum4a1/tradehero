package com.tradehero.common.billing.googleplay;

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

    public void fetchSkus()
    {
        // TODO whenever it should come from the server
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
}
