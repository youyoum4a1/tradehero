package com.tradehero.common.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.milestone.BaseMilestone;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract public class PurchaseFetchMilestone<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>,
        BillingExceptionType extends BillingException>
        extends BaseMilestone
{
    protected boolean running;
    protected boolean complete;
    protected boolean failed;
    protected BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType>
            purchaseFetcherHolder;
    protected BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> purchaseFetchedListener;
    protected int requestCode;
    protected List<ProductPurchaseType> fetchedPurchases;

    public PurchaseFetchMilestone(BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> purchaseFetcherHolder)
    {
        super();
        setBillingActor(purchaseFetcherHolder);
        purchaseFetchedListener = createPurchaseFetchedListener();
    }

    abstract protected BillingPurchaseFetcher.OnPurchaseFetchedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType> createPurchaseFetchedListener();

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

    public void setBillingActor(BillingPurchaseFetcherHolder<ProductIdentifierType, OrderIdType, ProductPurchaseType, BillingExceptionType> billingActor)
    {
        this.purchaseFetcherHolder = billingActor;
    }

    public List<ProductPurchaseType> getFetchedPurchases()
    {
        return Collections.unmodifiableList(fetchedPurchases);
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
