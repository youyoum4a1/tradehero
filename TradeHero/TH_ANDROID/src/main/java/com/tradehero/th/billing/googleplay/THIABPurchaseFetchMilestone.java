package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseFetchMilestone;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 11:36 AM To change this template use File | Settings | File Templates. */
public class THIABPurchaseFetchMilestone
        extends
        IABPurchaseFetchMilestone<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                BillingPurchaseFetcher.OnPurchaseFetchedListener<
                        IABSKU,
                        THIABOrderId,
                        THIABPurchase,
                        IABException>>
{
    public static final String TAG = THIABPurchaseFetchMilestone.class.getSimpleName();

    protected THIABLogicHolder logicHolder;

    /**
     * The billing actor should be strongly referenced elsewhere
     * @param logicHolder
     */
    public THIABPurchaseFetchMilestone(THIABLogicHolder logicHolder)
    {
        super(logicHolder.getPurchaseFetcherHolder());
        this.logicHolder = logicHolder;
    }

    @Override protected int getAvailableRequestCode()
    {
        return logicHolder.getUnusedRequestCode();
    }

    @Override protected BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> createPurchaseFetchedListener()
    {
        return new BillingPurchaseFetcher.OnPurchaseFetchedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
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
