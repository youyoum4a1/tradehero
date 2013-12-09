package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseFetchMilestone;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 11:36 AM To change this template use File | Settings | File Templates. */
public class THIABPurchaseFetchMilestone
        extends
        IABPurchaseFetchMilestone<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABPurchaseFetcher.OnPurchaseFetchedListener<
                        IABSKU,
                        THIABOrderId,
                        THIABPurchase>>
{
    public static final String TAG = THIABPurchaseFetchMilestone.class.getSimpleName();

    /**
     * The billing actor should be strongly referenced elsewhere
     * @param actorPurchaseFetcher
     */
    public THIABPurchaseFetchMilestone(THIABActorPurchaseFetcher actorPurchaseFetcher)
    {
        super(actorPurchaseFetcher);
    }

    @Override protected IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase> createPurchaseFetchedListener()
    {
        return new IABPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase>()
        {
            @Override public void onFetchPurchasesFailed(int requestCode, IABException exception)
            {
                failed = true;
                complete = false;
                running = false;
                notifyFailedListener(exception);
            }

            @Override public void onFetchedPurchases(int requestCode, Map<IABSKU, THIABPurchase> purchases)
            {
                failed = false;
                complete = true;
                running = false;
                fetchedPurchases = purchases;
                notifyCompleteListener();
            }
        };
    }
}
