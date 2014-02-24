package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.milestone.BaseMilestone;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 11:36 AM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseFetchMilestone<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
        extends BaseMilestone
{
    public static final String TAG = IABPurchaseFetchMilestone.class.getSimpleName();

    protected boolean running;
    protected boolean complete;
    protected boolean failed;
    protected WeakReference<IABPurchaseFetcherHolder<IABSKUType, IABOrderIdType, IABPurchaseType, IABPurchaseFetchedListenerType, IABException>>
            actorPurchaseFetcherWeak = new WeakReference<>(null);
    protected IABPurchaseFetchedListenerType purchaseFetchedListener;
    protected int requestCode;
    protected Map<IABSKUType, IABPurchaseType> fetchedPurchases;

    /**
     * The billing actor should be strongly referenced elsewhere
     * @param actorPurchaseFetcher
     */
    public IABPurchaseFetchMilestone(IABPurchaseFetcherHolder<IABSKUType, IABOrderIdType, IABPurchaseType, IABPurchaseFetchedListenerType, IABException> actorPurchaseFetcher)
    {
        super();
        setBillingActor(actorPurchaseFetcher);
        purchaseFetchedListener = createPurchaseFetchedListener();
    }

    abstract protected IABPurchaseFetchedListenerType createPurchaseFetchedListener();

    @Override public boolean isComplete()
    {
        return complete;
    }

    @Override public boolean isFailed()
    {
        return failed;
    }

    @Override public boolean isRunning()
    {
        return running;
    }

    public IABPurchaseFetcherHolder<IABSKUType, IABOrderIdType, IABPurchaseType, IABPurchaseFetchedListenerType, IABException> getBillingActor()
    {
        return actorPurchaseFetcherWeak.get();
    }

    /**
     * The actor should be strongly referenced elsewhere
     * @param billingActor
     */
    public void setBillingActor(IABPurchaseFetcherHolder<IABSKUType, IABOrderIdType, IABPurchaseType, IABPurchaseFetchedListenerType, IABException> billingActor)
    {
        this.actorPurchaseFetcherWeak = new WeakReference<>(billingActor);
    }

    public Map<IABSKUType, IABPurchaseType> getFetchedPurchases()
    {
        return Collections.unmodifiableMap(fetchedPurchases);
    }

    @Override public void launch()
    {
        this.requestCode = getBillingActor().registerPurchaseFetchedListener(purchaseFetchedListener);
        getBillingActor().launchFetchPurchaseSequence(requestCode);
    }

    @Override public void onDestroy()
    {
        purchaseFetchedListener = null;
    }
}
