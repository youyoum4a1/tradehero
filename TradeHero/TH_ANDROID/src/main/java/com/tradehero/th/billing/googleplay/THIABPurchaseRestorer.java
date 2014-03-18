package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseRestorer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.utils.DaggerUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 5:47 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseRestorer extends IABPurchaseRestorer<
        IABSKU,
        THIABProductDetail,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        THIABLogicHolder,
        THIABBillingRequestFull>
{
    @Inject protected CurrentActivityHolder currentActivityHolder;
    @Inject THIABLogicHolder logicHolder;
    private WeakReference<OnPurchaseRestorerFinishedListener> finishedListener = new WeakReference<>(null);
    protected int requestCodeReporter;
    private PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseReportedListener;
    private final List<THIABPurchase> failedReports;

    public THIABPurchaseRestorer(THIABLogicHolder logicHolder)
    {
        super(logicHolder);
        failedReports = new ArrayList<>();
        DaggerUtils.inject(this);
    }

    @Override public void init()
    {
        super.init();
        purchaseReportedListener = new PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
        {
            @Override public void onPurchaseReportFailed(int requestCode, THIABPurchase reportedPurchase, IABException error)
            {
                Timber.e("onPurchaseReportFailed", error);
                haveBillingActorForget(requestCode);
                failedReports.add(reportedPurchase);
                continueSequenceOrNotify();
            }

            @Override public void onPurchaseReported(int requestCode, THIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                Timber.d("onPurchaseReported");
                haveBillingActorForget(requestCode);
                launchOneConsumeSequence(reportedPurchase);
            }
        };
    }

    @Override protected Milestone createMilestone()
    {
        return new PurchaseRestorerRequiredMilestone(logicHolder);
    }

    @Override protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> createPurchaseConsumerListener()
    {
        return new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
        {
            @Override public void onPurchaseConsumed(int requestCode, THIABPurchase purchase)
            {
                Timber.d("onPurchaseConsumed");
                handlePurchaseConsumed(requestCode, purchase);
            }

            @Override public void onPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
            {
                Timber.d("onPurchaseConsumeFailed");
                handlePurchaseConsumeFailed(requestCode, purchase, exception);
            }
        };
    }

    @Override protected void handlePurchaseConsumed(int requestCode, THIABPurchase purchase)
    {
        super.handlePurchaseConsumed(requestCode, purchase);
        continueSequenceOrNotify();
    }

    @Override protected void handlePurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
    {
        super.handlePurchaseConsumeFailed(requestCode, purchase, exception);
        continueSequenceOrNotify();
    }

    public void onDestroy()
    {
        failedReports.clear();
        super.onDestroy();
    }

    protected void notifyPurchaseRestoreFinishedListener()
    {
        super.notifyPurchaseRestoreFinishedListener();
        OnIABPurchaseRestorerFinishedListener<THIABPurchase> finishedListener = getPurchaseRestoreFinishedListener();
        if (finishedListener instanceof OnPurchaseRestorerFinishedListener)
        {
            ((OnPurchaseRestorerFinishedListener) finishedListener).onPurchaseRestoreFinished(okPurchases, failedReports, failedConsumes);
        }
    }

    protected void launchWholeSequence()
    {
        remainingPurchasesToWorkOn = new ArrayList<> (((PurchaseRestorerRequiredMilestone) milestone).getFetchedPurchases().values());
        continueSequenceOrNotify();
    }

    protected void continueSequenceOrNotify()
    {
        if (remainingPurchasesToWorkOn.size() > 0)
        {
            launchOneReportSequence();
        }
        else
        {
            notifyPurchaseRestoreFinishedListener();
        }
    }

    protected void launchOneReportSequence()
    {
        THIABPurchase purchase = remainingPurchasesToWorkOn.get(0);
        remainingPurchasesToWorkOn.remove(purchase);
        THIABPurchaseReporterHolder actorReporter = logicHolder;
        if (actorReporter != null)
        {
            requestCodeReporter = logicHolder.getUnusedRequestCode();
            actorReporter.registerPurchaseReportedListener(requestCodeReporter, purchaseReportedListener);
            actorReporter.launchReportSequence(requestCodeReporter, purchase);
        }
        else
        {
            Timber.w("launchOneReportSequence: BillingLogicHolder just became null");
            failedReports.add(purchase);
            continueSequenceOrNotify();
        }
    }

    public static interface OnPurchaseRestorerFinishedListener extends OnIABPurchaseRestorerFinishedListener<THIABPurchase>
    {
        void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed);
    }
}
