package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABPurchaseRestorer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReporter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/25/13 Time: 5:47 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseRestorer extends IABPurchaseRestorer<
        IABSKU,
        THIABOrderId,
        BaseIABPurchase,
        THIABActorPurchaseConsumer,
        IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                IABSKU,
                THIABOrderId,
                BaseIABPurchase,
                IABException>>
{
    public static final String TAG = THIABPurchaseRestorer.class.getSimpleName();

    private final WeakReference<Activity> activity;
    private WeakReference<THIABActorInventoryFetcher> actorInventoryFetcher = new WeakReference<>(null);
    private WeakReference<THIABActorPurchaseFetcher> actorPurchaseFetcher = new WeakReference<>(null);
    private WeakReference<THIABActorPurchaseReporter> actorPurchaseReporter = new WeakReference<>(null);
    protected final UserBaseKey userBaseKey;
    private WeakReference<OnPurchaseRestorerFinishedListener> finishedListener = new WeakReference<>(null);
    protected int requestCodeReporter;
    private PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, BaseIABPurchase, Exception> purchaseReportedListener;
    private final List<BaseIABPurchase> failedReports;

    public THIABPurchaseRestorer(
            Activity activity,
            THIABActorInventoryFetcher actorInventoryFetcher,
            THIABActorPurchaseFetcher actorPurchaseFetcher,
            THIABActorPurchaseConsumer billingActorConsumer,
            THIABActorPurchaseReporter actorPurchaseReporter,
            UserBaseKey userBaseKey)
    {
        super(billingActorConsumer);
        this.activity = new WeakReference<>(activity);
        this.actorInventoryFetcher = new WeakReference<>(actorInventoryFetcher);
        this.actorPurchaseFetcher = new WeakReference<>(actorPurchaseFetcher);
        this.actorPurchaseReporter = new WeakReference<>(actorPurchaseReporter);
        this.userBaseKey = userBaseKey;
        failedReports = new ArrayList<>();
    }

    @Override public void init()
    {
        super.init();
        purchaseReportedListener = new PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, BaseIABPurchase, Exception>()
        {
            @Override public void onPurchaseReportFailed(int requestCode, BaseIABPurchase reportedPurchase, Exception error)
            {
                THLog.d(TAG, "onPurchaseReportFailed");
                haveBillingActorForget(requestCode);
                failedReports.add(reportedPurchase);
                continueSequenceOrNotify();
            }

            @Override public void onPurchaseReported(int requestCode, BaseIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                THLog.d(TAG, "onPurchaseReported");
                haveBillingActorForget(requestCode);
                launchOneConsumeSequence(reportedPurchase);
            }
        };
    }

    @Override protected Milestone createMilestone()
    {
        return new PurchaseRestorerRequiredMilestone(activity.get(), actorInventoryFetcher.get(), actorPurchaseFetcher.get(), userBaseKey);
    }

    @Override protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, BaseIABPurchase, IABException> createPurchaseConsumerListener()
    {
        return new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, BaseIABPurchase, IABException>()
        {
            @Override public void onPurchaseConsumed(int requestCode, BaseIABPurchase purchase)
            {
                THLog.d(TAG, "onPurchaseConsumed");
                handlePurchaseConsumed(requestCode, purchase);
            }

            @Override public void onPurchaseConsumeFailed(int requestCode, BaseIABPurchase purchase, IABException exception)
            {
                THLog.d(TAG, "onPurchaseConsumeFailed");
                handlePurchaseConsumeFailed(requestCode, purchase, exception);
            }
        };
    }

    @Override protected void handlePurchaseConsumed(int requestCode, BaseIABPurchase purchase)
    {
        super.handlePurchaseConsumed(requestCode, purchase);
        continueSequenceOrNotify();
    }

    @Override protected void handlePurchaseConsumeFailed(int requestCode, BaseIABPurchase purchase, IABException exception)
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
        OnIABPurchaseRestorerFinishedListener<IABSKU, THIABOrderId, BaseIABPurchase> finishedListener = getFinishedListener();
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
        BaseIABPurchase purchase = remainingPurchasesToWorkOn.get(0);
        remainingPurchasesToWorkOn.remove(purchase);
        THIABActorPurchaseReporter actorReporter = actorPurchaseReporter.get();
        if (actorReporter != null)
        {
            requestCodeReporter = actorReporter.registerPurchaseReportedHandler(purchaseReportedListener);
            actorReporter.launchReportSequence(requestCodeReporter, purchase);
        }
        else
        {
            THLog.w(TAG, "launchOneReportSequence: BillingActor just became null");
            failedReports.add(purchase);
            continueSequenceOrNotify();
        }
    }

    public static interface OnPurchaseRestorerFinishedListener extends OnIABPurchaseRestorerFinishedListener<IABSKU, THIABOrderId, BaseIABPurchase>
    {
        void onPurchaseRestoreFinished(List<BaseIABPurchase> consumed, List<BaseIABPurchase> reportFailed, List<BaseIABPurchase> consumeFailed);
    }
}
