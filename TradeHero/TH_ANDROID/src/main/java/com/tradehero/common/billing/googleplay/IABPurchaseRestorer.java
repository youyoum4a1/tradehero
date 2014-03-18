package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseRestorer;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABRestorePurchaseMilestoneFailedException;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.milestone.Milestone;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 5:47 PM To change this template use File | Settings | File Templates. */
abstract public class IABPurchaseRestorer<
        IABSKUType extends IABSKU,
        IABProductDetailType extends IABProductDetail<IABSKUType>,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        IABLogicHolderType extends IABLogicHolder<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                BillingRequestType,
                IABException>,
        BillingRequestType extends BillingRequest<
                IABSKUType,
                IABProductDetailType,
                IABPurchaseOrderType,
                IABOrderIdType,
                IABPurchaseType,
                IABException>>
    implements BillingPurchaseRestorer<
        IABSKUType,
        IABOrderIdType,
        IABPurchaseType>
{
    protected IABLogicHolderType logicHolder;
    protected OnIABPurchaseRestorerFinishedListener<IABPurchaseType> purchaseRestoreFinishedListener;
    protected Milestone milestone;
    protected Milestone.OnCompleteListener milestoneListener;
    protected int requestCodeConsumer;
    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABException> purchaseConsumerListener;
    protected List<IABPurchaseType> remainingPurchasesToWorkOn;
    protected final List<IABPurchaseType> okPurchases;
    protected final List<IABPurchaseType> failedConsumes;

    public IABPurchaseRestorer(IABLogicHolderType logicHolder)
    {
        this.logicHolder = logicHolder;
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
    abstract protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKUType,
            IABOrderIdType,
            IABPurchaseType,
            IABException> createPurchaseConsumerListener();

    public void onDestroy()
    {
        milestone = null;
        milestoneListener = null;
        remainingPurchasesToWorkOn.clear();
        okPurchases.clear();
        failedConsumes.clear();
    }

    protected void haveBillingActorForget(int requestCode)
    {
        IABLogicHolderType consumerHolderCopy = logicHolder;
        if (consumerHolderCopy != null)
        {
            consumerHolderCopy.forgetRequestCode(requestCode);
        }
    }

    public OnIABPurchaseRestorerFinishedListener<IABPurchaseType> getPurchaseRestoreFinishedListener()
    {
        return purchaseRestoreFinishedListener;
    }

    public void setPurchaseRestoreFinishedListener(
            OnIABPurchaseRestorerFinishedListener<IABPurchaseType> finishedListener)
    {
        this.purchaseRestoreFinishedListener = finishedListener;
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
        IABLogicHolderType purchaseConsumerHolder = logicHolder;
        if (!purchase.getType().equals(IABConstants.ITEM_TYPE_INAPP))
        {
            Timber.d("No point in consuming this purchase");
            // No need to add to okPurchases.add(purchase);
            continueSequenceOrNotify();
        }
        else if (purchaseConsumerHolder != null)
        {
            requestCodeConsumer = logicHolder.getUnusedRequestCode();
            purchaseConsumerHolder.registerConsumptionFinishedListener(requestCodeConsumer, purchaseConsumerListener);
            purchaseConsumerHolder.launchConsumeSequence(requestCodeConsumer, purchase);
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
