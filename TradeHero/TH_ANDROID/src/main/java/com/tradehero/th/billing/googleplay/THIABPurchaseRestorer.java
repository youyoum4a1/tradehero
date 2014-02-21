package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseRestorer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReporter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 5:47 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseRestorer extends IABPurchaseRestorer<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THIABActorPurchaseConsumer,
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABException>>
{
    private final WeakReference<Activity> activity;
    private WeakReference<THIABInventoryFetcherHolder> actorInventoryFetcher = new WeakReference<>(null);
    private WeakReference<THIABActorPurchaseFetcher> actorPurchaseFetcher = new WeakReference<>(null);
    private WeakReference<THIABPurchaseReporterHolder> actorPurchaseReporter = new WeakReference<>(null);
    private WeakReference<OnPurchaseRestorerFinishedListener> finishedListener = new WeakReference<>(null);
    protected int requestCodeReporter;
    private PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseReportedListener;
    private final List<THIABPurchase> failedReports;

    public THIABPurchaseRestorer(
            Activity activity,
            THIABInventoryFetcherHolder actorInventoryFetcher,
            THIABActorPurchaseFetcher actorPurchaseFetcher,
            THIABActorPurchaseConsumer billingActorConsumer,
            THIABPurchaseReporterHolder actorPurchaseReporter)
    {
        super(billingActorConsumer);
        this.activity = new WeakReference<>(activity);
        this.actorInventoryFetcher = new WeakReference<>(actorInventoryFetcher);
        this.actorPurchaseFetcher = new WeakReference<>(actorPurchaseFetcher);
        this.actorPurchaseReporter = new WeakReference<>(actorPurchaseReporter);
        failedReports = new ArrayList<>();
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
        return new PurchaseRestorerRequiredMilestone(activity.get(), actorInventoryFetcher.get(), actorPurchaseFetcher.get());
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

    protected void notifyFinishedListener()
    {
        super.notifyFinishedListener();
        OnIABPurchaseRestorerFinishedListener<IABSKU, THIABOrderId, THIABPurchase> finishedListener = getFinishedListener();
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
            notifyFinishedListener();
        }
    }

    protected void launchOneReportSequence()
    {
        THIABPurchase purchase = remainingPurchasesToWorkOn.get(0);
        remainingPurchasesToWorkOn.remove(purchase);
        THIABPurchaseReporterHolder actorReporter = actorPurchaseReporter.get();
        if (actorReporter != null)
        {
            requestCodeReporter = actorReporter.registerPurchaseReportedListener(purchaseReportedListener);
            actorReporter.launchReportSequence(requestCodeReporter, purchase);
        }
        else
        {
            Timber.w("launchOneReportSequence: BillingLogicHolder just became null");
            failedReports.add(purchase);
            continueSequenceOrNotify();
        }
    }

    public static interface OnPurchaseRestorerFinishedListener extends OnIABPurchaseRestorerFinishedListener<IABSKU, THIABOrderId, THIABPurchase>
    {
        void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed);
    }
}
