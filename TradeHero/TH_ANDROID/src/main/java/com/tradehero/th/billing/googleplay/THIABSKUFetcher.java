package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.BaseSKUFetcher;
import com.tradehero.th.billing.SKUFetcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 4:58 PM To change this template use File | Settings | File Templates. */
public class THIABSKUFetcher extends BaseSKUFetcher<
        IABSKU,
        SKUFetcher.OnSKUFetchedListener<IABSKU>>
    implements IABSKUFetcher<IABSKU, SKUFetcher.OnSKUFetchedListener<IABSKU>>
{
    public static final String TAG = THIABSKUFetcher.class.getSimpleName();

    public static final String EXTRA_CASH_T0_KEY = "com.myhero.th.extracash.t0";
    public static final String EXTRA_CASH_T1_KEY = "com.myhero.th.extracash.t1";
    public static final String EXTRA_CASH_T2_KEY = "com.myhero.th.extracash.t2";

    public static final String CREDIT_1 = "com.myhero.th.1cc";
    //public static final String CREDIT_5 = "com.myhero.th.5cc";
    public static final String CREDIT_10 = "com.myhero.th.10cc";
    public static final String CREDIT_20 = "com.myhero.th.20cc";

    public static final String RESET_PORTFOLIO_0 = "com.myhero.th.resetportfolio.0";
    public static final String ALERT_1 = "com.myhero.th.stockalert.subscription.t0";
    public static final String ALERT_5 = "com.myhero.th.stockalert.subscription.t1";
    public static final String ALERT_UNLIMITED = "com.myhero.th.stockalert.subscription.t2";

    public THIABSKUFetcher()
    {
        super();
        // TODO hard-coded while there is nothing coming from the server.
        List<IABSKU> inAppIABSKUs = new ArrayList<>();
        inAppIABSKUs.add(new IABSKU(EXTRA_CASH_T0_KEY));
        inAppIABSKUs.add(new IABSKU(EXTRA_CASH_T1_KEY));
        inAppIABSKUs.add(new IABSKU(EXTRA_CASH_T2_KEY));
        inAppIABSKUs.add(new IABSKU(CREDIT_1));
        //inAppIABSKUs.add(new IABSKU(CREDIT_5));
        inAppIABSKUs.add(new IABSKU(CREDIT_10));
        inAppIABSKUs.add(new IABSKU(CREDIT_20));

        inAppIABSKUs.add(new IABSKU(ALERT_1));
        inAppIABSKUs.add(new IABSKU(ALERT_5));
        inAppIABSKUs.add(new IABSKU(ALERT_UNLIMITED));

        inAppIABSKUs.add(new IABSKU(RESET_PORTFOLIO_0));
        availableSkus.put(Constants.ITEM_TYPE_INAPP, inAppIABSKUs);

        availableSkus.put(Constants.ITEM_TYPE_SUBS, new ArrayList<IABSKU>());
    }

    @Override public void fetchSkus(int requestCode)
    {
        this.requestCode = requestCode;
        notifyListenerFetched();
    }

    @Override public Map<String, List<IABSKU>> fetchSkusSync()
    {
        return Collections.unmodifiableMap(availableSkus);
    }

    public Map<String, List<IABSKU>> getAvailableSkus()
    {
        // TODO find out whether the lists are modifiable
        return Collections.unmodifiableMap(availableSkus);
    }
}
