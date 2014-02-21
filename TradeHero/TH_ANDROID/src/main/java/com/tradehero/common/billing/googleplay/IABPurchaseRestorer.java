package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABRestorePurchaseMilestoneFailedException;
import com.tradehero.common.milestone.Milestone;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 5:47 PM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseRestorer<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABPurchaseConsumerHolderType extends IABPurchaseConsumerHolder<
                        IABSKUType,
                        IABOrderIdType,
                        IABPurchaseType,
                        IABConsumeFinishedListenerType,
                        IABException>,
        IABConsumeFinishedListenerType extends IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKUType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    implements BillingPurchaseRestorer<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
{
    protected WeakReference<IABPurchaseConsumerHolderType> consumerHolder = new WeakReference<>(null);
    protected WeakReference<OnIABPurchaseRestorerFinishedListener<IABPurchaseType>> purchaseRestoreFinishedListener = new WeakReference<>(null);
    protected Milestone milestone;
    protected Milestone.OnCompleteListener milestoneListener;
    protected int requestCodeConsumer;
    protected IABConsumeFinishedListenerType purchaseConsumerListener;
    protected List<IABPurchaseType> remainingPurchasesToWorkOn;
    protected final List<IABPurchaseType> okPurchases;
    protected final List<IABPurchaseType> failedConsumes;

    public IABPurchaseRestorer(IABPurchaseConsumerHolderType consumeHolder)
    {
        setConsumerHolder(consumeHolder);
        okPurchases = new ArrayList<>();
        failedConsumes = new ArrayList<>();
    }

    public void init()
    {
        milestone = createMilestone();
        milestoneListener = new Milestone.OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                Timber.d("onComplete Milestone");
                launchWholeSequence();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                Timber.d("onFailed Milestone");
                notifyPurchaseRestoreFailedListener(new IABRestorePurchaseMilestoneFailedException(throwable));
            }
        };
        milestone.setOnCompleteListener(milestoneListener);
        purchaseConsumerListener = createPurchaseConsumerListener();
    }

    abstract protected Milestone createMilestone();
    abstract protected IABConsumeFinishedListenerType createPurchaseConsumerListener();

    public void onDestroy()
    {
        milestone = null;
        milestoneListener = null;
        remainingPurchasesToWorkOn.clear();
        okPurchases.clear();
        failedConsumes.clear();
    }

    protected IABPurchaseConsumerHolderType getConsumerHolder()
    {
        return consumerHolder.get();
    }

    protected void setConsumerHolder(IABPurchaseConsumerHolderType consumerHolder)
    {
        this.consumerHolder = new WeakReference<>(consumerHolder);
    }

    protected void haveBillingActorForget(int requestCode)
    {
        IABPurchaseConsumerHolderType consumerHolderCopy = getConsumerHolder();
        if (consumerHolderCopy != null)
        {
            consumerHolderCopy.unregisterConsumeFinishedListener(requestCode);
        }
    }

    public OnIABPurchaseRestorerFinishedListener<IABPurchaseType> getPurchaseRestoreFinishedListener()
    {
        return purchaseRestoreFinishedListener.get();
    }

    public void setPurchaseRestoreFinishedListener(
            OnIABPurchaseRestorerFinishedListener<IABPurchaseType> finishedListener)
    {
        this.purchaseRestoreFinishedListener = new WeakReference<>(finishedListener);
    }

    public void launchRestorePurchaseSequence()
    {
        milestone.launch();
    }

    protected void notifyPurchaseRestoreFinishedListener()
    {
        OnIABPurchaseRestorerFinishedListener<IABPurchaseType> finishedListener = getPurchaseRestoreFinishedListener();
        if (finishedListener != null)
        {
            finishedListener.onPurchaseRestoreFinished(okPurchases, failedConsumes);
        }
    }

    protected void notifyPurchaseRestoreFailedListener(IABException iabException)
    {
        OnIABPurchaseRestorerFinishedListener finishedListener = getPurchaseRestoreFinishedListener();
        if (finishedListener != null)
        {
            finishedListener.onPurchaseRestoreFailed(iabException);
        }
    }

    abstract protected void launchWholeSequence();
    abstract protected void continueSequenceOrNotify();

    protected void launchOneConsumeSequence(IABPurchaseType purchase)
    {
        IABPurchaseConsumerHolderType billingActor = getConsumerHolder();
        if (!purchase.getType().equals(IABConstants.ITEM_TYPE_INAPP))
        {
            Timber.d("No point in consuming this purchase");
            // No need to add to okPurchases.add(purchase);
            continueSequenceOrNotify();
        }
        else if (billingActor != null)
        {
            requestCodeConsumer = billingActor.registerConsumeFinishedListener(purchaseConsumerListener);
            billingActor.launchConsumeSequence(requestCodeConsumer, purchase);
        }
        else
        {
            Timber.w("launchOneConsumeSequence: BillingLogicHolder just became null");
            failedConsumes.add(purchase);
            continueSequenceOrNotify();
        }
    }

    protected void handlePurchaseConsumed(int requestCode, IABPurchaseType purchase)
    {
        haveBillingActorForget(requestCode);
        okPurchases.add(purchase);
        // Child class needs to decide on the next action
    }

    protected void handlePurchaseConsumeFailed(int requestCode, IABPurchaseType purchase, IABException exception)
    {
        haveBillingActorForget(requestCode);
        failedConsumes.add(purchase);
        // Child class needs to decide on the next action
    }

    public static interface OnIABPurchaseRestorerFinishedListener<IABPurchaseType>
    {
        void onPurchaseRestoreFinished(List<IABPurchaseType> consumed, List<IABPurchaseType> consumeFailed);
        void onPurchaseRestoreFailed(IABException iabException);
    }
}
