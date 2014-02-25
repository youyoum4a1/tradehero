package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.milestone.BaseMilestone;
import java.util.Collections;
import java.util.Map;

/**
 * Created by xavier on 2/25/14.
 */
abstract public class PurchaseFetchMilestone<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        PurchaseFetchedListenerType extends BillingPurchaseFetcher.OnPurchaseFetchedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
        extends BaseMilestone
{
    protected boolean running;
    protected boolean complete;
    protected boolean failed;
    protected BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, PurchaseFetchedListenerType, BillingExceptionType>
            purchaseFetcherHolder;
    protected PurchaseFetchedListenerType purchaseFetchedListener;
    protected int requestCode;
    protected Map<ProductIdentifierType, ProductPurchaseType> fetchedPurchases;

    public PurchaseFetchMilestone(BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, PurchaseFetchedListenerType, BillingExceptionType> purchaseFetcherHolder)
    {
        super();
        setBillingActor(purchaseFetcherHolder);
        purchaseFetchedListener = createPurchaseFetchedListener();
    }

    abstract protected PurchaseFetchedListenerType createPurchaseFetchedListener();

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

    public void setBillingActor(BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, PurchaseFetchedListenerType, BillingExceptionType> billingActor)
    {
        this.purchaseFetcherHolder = billingActor;
    }

    public Map<ProductIdentifierType, ProductPurchaseType> getFetchedPurchases()
    {
        return Collections.unmodifiableMap(fetchedPurchases);
    }

    @Override public void launch()
    {
        this.requestCode = getAvailableRequestCode();
        purchaseFetcherHolder.registerPurchaseFetchedListener(this.requestCode, purchaseFetchedListener);
        purchaseFetcherHolder.launchFetchPurchaseSequence(this.requestCode);
    }

    abstract protected int getAvailableRequestCode();

    @Override public void onDestroy()
    {
        purchaseFetchedListener = null;
        purchaseFetcherHolder = null;
    }
}
