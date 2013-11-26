package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THLog;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 5:47 PM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseRestorer<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABActorPurchaseConsumerType extends IABActorPurchaseConsumer<
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
{
    public static final String TAG = IABPurchaseRestorer.class.getSimpleName();

    protected WeakReference<IABActorPurchaseConsumerType> consumeActor = new WeakReference<>(null);
    protected WeakReference<OnIABPurchaseRestorerFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType>> finishedListener = new WeakReference<>(null);
    protected Milestone milestone;
    protected Milestone.OnCompleteListener milestoneListener;
    protected int requestCodeConsumer;
    protected IABConsumeFinishedListenerType purchaseConsumerListener;
    protected List<IABPurchaseType> remainingPurchasesToWorkOn;
    protected final List<IABPurchaseType> okPurchases;
    protected final List<IABPurchaseType> failedConsumes;

    public IABPurchaseRestorer(IABActorPurchaseConsumerType consumeActor)
    {
        setConsumeActor(consumeActor);
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
                THLog.d(TAG, "onComplete Milestone");
                launchWholeSequence();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                THLog.d(TAG, "onFailed Milestone");
                notifyFailedListener(throwable);
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

    protected IABActorPurchaseConsumerType getConsumeActor()
    {
        return consumeActor.get();
    }

    protected void setConsumeActor(IABActorPurchaseConsumerType consumeActor)
    {
        this.consumeActor = new WeakReference<>(consumeActor);
    }

    protected void haveBillingActorForget(int requestCode)
    {
        IABActorPurchaseConsumerType billingActor = getConsumeActor();
        if (billingActor != null)
        {
            billingActor.forgetRequestCode(requestCode);
        }
    }

    public OnIABPurchaseRestorerFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType> getFinishedListener()
    {
        return finishedListener.get();
    }

    public void setFinishedListener(OnIABPurchaseRestorerFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType> finishedListener)
    {
        this.finishedListener = new WeakReference<>(finishedListener);
    }

    public void launchRestorePurchaseSequence()
    {
        milestone.launch();
    }

    protected void notifyFinishedListener()
    {
        OnIABPurchaseRestorerFinishedListener<IABSKUType, IABOrderIdType, IABPurchaseType> finishedListener = getFinishedListener();
        if (finishedListener != null)
        {
            finishedListener.onPurchaseRestoreFinished(okPurchases, failedConsumes);
        }
    }

    protected void notifyFailedListener(Throwable throwable)
    {
        OnIABPurchaseRestorerFinishedListener finishedListener = getFinishedListener();
        if (finishedListener != null)
        {
            finishedListener.onPurchaseRestoreFailed(throwable);
        }
    }

    abstract protected void launchWholeSequence();
    abstract protected void continueSequenceOrNotify();

    protected void launchOneConsumeSequence(IABPurchaseType purchase)
    {
        IABActorPurchaseConsumerType billingActor = getConsumeActor();
        if (billingActor != null)
        {
            requestCodeConsumer = billingActor.registerConsumeFinishedListener(purchaseConsumerListener);
            billingActor.launchConsumeSequence(requestCodeConsumer, purchase);
        }
        else
        {
            THLog.w(TAG, "launchOneConsumeSequence: BillingActor just became null");
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

    public static interface OnIABPurchaseRestorerFinishedListener<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
    {
        void onPurchaseRestoreFinished(List<IABPurchaseType> consumed, List<IABPurchaseType> consumeFailed);
        void onPurchaseRestoreFailed(Throwable throwable);
    }
}
