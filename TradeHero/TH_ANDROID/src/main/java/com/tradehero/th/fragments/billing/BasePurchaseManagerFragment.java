package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exceptions.IABAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exceptions.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.IABAlertDialogSKUUtil;
import com.tradehero.th.billing.googleplay.IABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THIABActorPurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABActorPurchaser;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetails;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.billing.googleplay.THSKUDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 * It expects its Activity to implement THIABActorUser.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseManagerFragment extends DashboardFragment
        implements IABAlertDialogUtil.OnDialogSKUDetailsClickListener<THIABProductDetails>,
        THIABActorUser
{
    public static final String TAG = BasePurchaseManagerFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_USER_ID = BasePurchaseManagerFragment.class.getName() + ".userId";
    public static final String BUNDLE_KEY_PORTFOLIO_ID = BasePurchaseManagerFragment.class.getName() + ".portfolioId";

    private ShowSkuDetailsMilestone showSkuDetailsMilestone;
    private Milestone.OnCompleteListener showSkuDetailsMilestoneListener;
    private Runnable runOnShowSkuDetailsMilestoneComplete;
    protected Throwable showSkuDetailsMilestoneException;

    private ProgressDialog progressDialog;
    @Inject Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;
    @Inject Lazy<UserProfileCache> userProfileCache;

    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    private DTOCache.Listener<UserBaseKey, OwnedPortfolioIdList> portfolioIdListListener;
    private DTOCache.GetOrFetchTask<OwnedPortfolioIdList> portfolioIdListFetchTask;

    @Inject Lazy<THSKUDetailCache> skuDetailCache;
    protected WeakReference<THIABActor> billingActor = new WeakReference<>(null);
    protected InventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetails, IABException> inventoryFetchedForgetListener;
    protected UserBaseKey userBaseKey;
    protected PortfolioId portfolioId;
    private Runnable runOnPurchaseComplete;

    protected BillingPurchaser.OnPurchaseFinishedListener<
        IABSKU,
        THIABPurchaseOrder,
        THIABOrderId,
        BaseIABPurchase,
        IABException> purchaseFinishedListener;
    protected PurchaseReporter.OnPurchaseReportedListener<
        IABSKU,
        THIABOrderId,
        BaseIABPurchase,
        Exception> purchaseReportedListener;
    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<
        IABSKU,
        THIABOrderId,
        BaseIABPurchase,
        IABException> consumptionFinishedListener;

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setBillingActor(((THIABActorUser) getActivity()).getBillingActor());
    }

    @Override public void onResume()
    {
        super.onResume();
        prepareCallbacks();
        preparePrerequisites();
    }

    @Override public void onPause()
    {
        inventoryFetchedForgetListener = null;

        if (portfolioIdListFetchTask != null)
        {
            portfolioIdListFetchTask.forgetListener(true);
        }
        portfolioIdListFetchTask = null;
        portfolioIdListListener = null;
        runOnShowSkuDetailsMilestoneComplete = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        THLog.d(TAG, "onDestroyView");
        if (progressDialog != null)
        {
            progressDialog.hide();
            progressDialog = null;
        }
        purchaseFinishedListener = null;
        purchaseReportedListener = null;
        consumptionFinishedListener = null;
        super.onDestroyView();
    }

    private void prepareCallbacks()
    {
        if (purchaseFinishedListener == null)
        {
            purchaseFinishedListener = new BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, BaseIABPurchase, IABException>()
            {
                @Override public void onPurchaseFailed(int requestCode, THIABPurchaseOrder purchaseOrder, IABException exception)
                {
                    haveActorForget(requestCode);
                    runOnPurchaseComplete = null;
                    THLog.e(TAG, "onPurchaseFailed requestCode " + requestCode, exception);
                    if (exception instanceof IABVerificationFailedException)
                    {
                        IABAlertDialogUtil.popVerificationFailed(getActivity());
                    }
                    else if (exception instanceof IABUserCancelledException)
                    {
                        IABAlertDialogUtil.popUserCancelled(getActivity());
                    }
                    else if (exception instanceof IABBadResponseException)
                    {
                        IABAlertDialogUtil.popBadResponse(getActivity());
                    }
                    else if (exception instanceof IABRemoteException)
                    {
                        IABAlertDialogUtil.popRemoteError(getActivity());
                    }
                    else if (exception instanceof IABAlreadyOwnedException)
                    {
                        IABAlertDialogUtil.popSKUAlreadyOwned(getActivity(), skuDetailCache.get().get(purchaseOrder.getProductIdentifier()));
                    }
                    else if (exception instanceof IABSendIntentException)
                    {
                        IABAlertDialogUtil.popSendIntent(getActivity());
                    }
                    else
                    {
                        IABAlertDialogUtil.popUnknownError(getActivity());
                    }
                }

                @Override public void onPurchaseFinished(int requestCode, THIABPurchaseOrder purchaseOrder, BaseIABPurchase purchase)
                {
                    haveActorForget(requestCode);
                    launchReportPurchaseSequence(purchase);
                }
            };
        }

        if (purchaseReportedListener == null)
        {
            purchaseReportedListener = new PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, BaseIABPurchase, Exception>()
            {
                @Override public void onPurchaseReported(int requestCode, BaseIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
                {
                    haveActorForget(requestCode);
                    handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
                }

                @Override public void onPurchaseReportFailed(int requestCode, BaseIABPurchase reportedPurchase, Exception error)
                {
                    haveActorForget(requestCode);
                    runOnPurchaseComplete = null;
                    THLog.e(TAG, "Failed to report to server", error);
                    if (progressDialog != null)
                    {
                        progressDialog.hide();
                    }
                    IABAlertDialogUtil.popFailedToReport(getActivity());
                }
            };
        }

        if (consumptionFinishedListener == null)
        {
            consumptionFinishedListener = new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, BaseIABPurchase, IABException>()
            {
                @Override public void onPurchaseConsumeFailed(int requestCode, BaseIABPurchase purchase, IABException exception)
                {
                    haveActorForget(requestCode);
                    runOnPurchaseComplete = null;
                    THLog.e(TAG, "Failed to consume purchase", exception);
                    if (progressDialog != null)
                    {
                        progressDialog.hide();
                    }
                    IABAlertDialogUtil.popOfferSendEmailSupportConsumeFailed(getActivity(), exception);
                }

                @Override public void onPurchaseConsumed(int requestCode, BaseIABPurchase purchase)
                {
                    haveActorForget(requestCode);
                    handlePurchaseConsumed(purchase);
                }
            };
        }
    }

    protected void haveActorForget(int requestCode)
    {
        THIABActor actor = getBillingActor();
        if (actor != null)
        {
            actor.forgetRequestCode(requestCode);
        }
    }

    protected void preparePrerequisites()
    {
        this.portfolioId = null;
        Integer portfolioId = null;

        Bundle args = getArguments();

        if (args != null && args.containsKey(BUNDLE_KEY_USER_ID))
        {
            userBaseKey = new UserBaseKey(args.getInt(BUNDLE_KEY_USER_ID));
        }
        else
        {
            userBaseKey = new UserBaseKey(currentUserBaseKeyHolder.get().getCurrentUserBaseKey().key);
        }

        if (args != null && args.containsKey(BUNDLE_KEY_PORTFOLIO_ID))
        {
            portfolioId = args.getInt(BUNDLE_KEY_PORTFOLIO_ID);
        }
        else
        {
            OwnedPortfolioId ownedPortfolioId = portfolioCompactListCache.get().getDefaultPortfolio(userBaseKey);
            if (ownedPortfolioId != null)
            {
                portfolioId = ownedPortfolioId.portfolioId;
            }
        }

        if (portfolioId == null)
        {
            // We still need to collect the portfolios
            showSkuDetailsMilestone = new ShowSkuDetailsMilestone(getActivity(), getBillingActor(), IABSKUListType.getInApp(), userBaseKey);
        }
        else
        {
            this.portfolioId = new PortfolioId(portfolioId);
            showSkuDetailsMilestone = new ShowSkuDetailsMilestone(getActivity(), getBillingActor(), IABSKUListType.getInApp(), null);
        }
        showSkuDetailsMilestoneListener = createShowSkuDetailsMilestoneListener();
        showSkuDetailsMilestone.setOnCompleteListener(showSkuDetailsMilestoneListener);
        showSkuDetailsMilestoneException = null;
        showSkuDetailsMilestone.launch();
    }

    protected Milestone.OnCompleteListener createShowSkuDetailsMilestoneListener()
    {
        return new Milestone.OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                if (!(milestone instanceof ShowSkuDetailsMilestone))
                {
                    THLog.e(TAG, "We did not receive the proper milestone type: " + milestone.getClass().getName(), new Exception());
                }
                else
                {
                    handleShowSkuDetailsMilestoneComplete();
                }
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                THLog.e(TAG, "Failed to complete ShowSkuDetailsMilestone", throwable);
                showSkuDetailsMilestoneException = throwable;
                handleShowSkuDetailsMilestoneFailed(throwable);
            }
        };
    }

    abstract protected void handleShowSkuDetailsMilestoneFailed(Throwable throwable);

    protected void handleShowSkuDetailsMilestoneComplete()
    {
        // At this stage, we know the applicable portfolio is available in the cache
        if (this.portfolioId == null)
        {
            this.portfolioId = portfolioCompactListCache.get().getDefaultPortfolio(userBaseKey).getPortfolioId();
        }

        runWhatWaitingForSkuDetailsMilestone();
    }

    protected void runWhatWaitingForSkuDetailsMilestone()
    {
        Runnable runnable = runOnShowSkuDetailsMilestoneComplete;
        if (runnable != null)
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            runOnShowSkuDetailsMilestoneComplete = null;
            runnable.run();
        }
    }

    protected void waitForSkuDetailsMilestoneComplete(Runnable runnable)
    {
        if (showSkuDetailsMilestone.isComplete())
        {
            if (runnable != null)
            {
                runnable.run();
            }
        }
        else
        {
            if (runnable != null)
            {
                popDialogLoadingInfo();
                runOnShowSkuDetailsMilestoneComplete = runnable;
            }
            if (showSkuDetailsMilestone.isFailed() || !showSkuDetailsMilestone.isRunning())
            {
                showSkuDetailsMilestone.launch();
            }
        }
    }

    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return new OwnedPortfolioId(userBaseKey, portfolioId);
    }

    protected boolean isBillingAvailable()
    {
        return getBillingActor().isBillingAvailable();
    }

    protected boolean hadErrorLoadingInventory()
    {
        return getBillingActor().hadErrorLoadingInventory();
    }

    protected boolean isInventoryReady()
    {
        return getBillingActor().isInventoryReady();
    }

    //<editor-fold desc="THIABActorUser">
    public THIABActor getBillingActor()
    {
        return billingActor.get();
    }

    public void setBillingActor(THIABActor billingActor)
    {
        this.billingActor = new WeakReference<>(billingActor);
    }
    //</editor-fold>

    protected boolean popErrorConditional()
    {
        if (!isBillingAvailable())
        {
            IABAlertDialogUtil.popBillingUnavailable(getActivity());
        }
        else if (hadErrorLoadingInventory())
        {
            popErrorWhenLoading();
        }
        else
        {
            // All clear
            return false;
        }
        return true;
    }

    protected void popErrorWhenLoading()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(R.string.store_billing_error_loading_window_title)
                .setMessage(R.string.store_billing_error_loading_window_description)
                .setCancelable(true)
                .setPositiveButton(R.string.store_billing_error_loading_act, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (inventoryFetchedForgetListener == null)
                        {
                            inventoryFetchedForgetListener = createForgetFetchedListener();
                        }
                        int requestCode = getBillingActor().registerInventoryFetchedListener(inventoryFetchedForgetListener);
                        getBillingActor().launchInventoryFetchSequence(requestCode);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected InventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetails, IABException> createForgetFetchedListener()
    {
        return new InventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetails, IABException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<IABSKU> productIdentifiers, Map<IABSKU, THIABProductDetails> inventory)
            {
                getBillingActor().forgetRequestCode(requestCode);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<IABSKU> productIdentifiers, IABException exception)
            {
                getBillingActor().forgetRequestCode(requestCode);
            }
        };
    }

    //<editor-fold desc="Pop SKU list">
    protected void conditionalPopBuyVirtualDollars()
    {
        conditionalPopBuyVirtualDollars(null);
    }

    protected void conditionalPopBuyVirtualDollars(Runnable runOnPurchaseComplete)
    {
        if (!popErrorConditional())
        {
            popBuyVirtualDollars(runOnPurchaseComplete);
        }
    }

    protected void popBuyVirtualDollars()
    {
        popBuyVirtualDollars(null);
    }

    protected void popBuyVirtualDollars(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetails.DOMAIN_VIRTUAL_DOLLAR, R.string.store_buy_virtual_dollar_window_title, runOnPurchaseComplete);
    }

    protected void conditionalPopBuyFollowCredits()
    {
        conditionalPopBuyFollowCredits(null);
    }

    protected void conditionalPopBuyFollowCredits(Runnable runOnPurchaseComplete)
    {
        if (!popErrorConditional())
        {
            popBuyFollowCredits(runOnPurchaseComplete);
        }
    }

    protected void popBuyFollowCredits()
    {
        popBuyFollowCredits(null);
    }

    protected void popBuyFollowCredits(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetails.DOMAIN_FOLLOW_CREDITS, R.string.store_buy_follow_credits_window_message, runOnPurchaseComplete);
    }

    protected void conditionalPopBuyStockAlerts()
    {
        conditionalPopBuyStockAlerts(null);
    }

    protected void conditionalPopBuyStockAlerts(Runnable runOnPurchaseComplete)
    {
        if (!popErrorConditional())
        {
            popBuyStockAlerts(runOnPurchaseComplete);
        }
    }

    protected void popBuyStockAlerts()
    {
        popBuyStockAlerts(null);
    }

    protected void popBuyStockAlerts(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetails.DOMAIN_STOCK_ALERTS, R.string.store_buy_stock_alerts_window_title, runOnPurchaseComplete);
    }

    protected void conditionalPopBuyResetPortfolio()
    {
        conditionalPopBuyResetPortfolio(null);
    }

    protected void conditionalPopBuyResetPortfolio(Runnable runOnPurchaseComplete)
    {
        if (!popErrorConditional())
        {
            popBuyResetPortfolio(runOnPurchaseComplete);
        }
    }

    protected void popBuyResetPortfolio()
    {
        popBuyResetPortfolio(null);
    }

    protected void popBuyResetPortfolio(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetails.DOMAIN_RESET_PORTFOLIO, R.string.store_buy_reset_portfolio_window_title, runOnPurchaseComplete);
    }

    protected void popBuyDialog(final String skuDomain, final int titleResId)
    {
        popBuyDialog(skuDomain, titleResId, null);
    }

    protected void popBuyDialog(final String skuDomain, final int titleResId, final Runnable runOnPurchaseComplete)
    {
        waitForSkuDetailsMilestoneComplete(new Runnable()
        {
            @Override public void run()
            {
                getView().post(new Runnable()
                {
                    @Override public void run()
                    {
                        IABAlertDialogSKUUtil.popBuyDialog(getActivity(), getBillingActor(), BasePurchaseManagerFragment.this, skuDomain, titleResId,
                                runOnPurchaseComplete);
                    }
                });
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="IABAlertDialogUtil.OnDialogSKUDetailsClickListener">
    @Override public void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, THIABProductDetails skuDetails, Runnable runOnPurchaseComplete)
    {
        this.runOnPurchaseComplete = runOnPurchaseComplete;
        launchPurchaseSequence(new THIABPurchaseOrder(skuDetails.getProductIdentifier(), getApplicablePortfolioId()));
    }
    //</editor-fold>

    protected void launchPurchaseSequence(THIABPurchaseOrder purchaseOrder)
    {
        launchPurchaseSequence(getBillingActor(), purchaseOrder);
    }

    protected void launchPurchaseSequence(THIABActorPurchaser actorPurchaser, THIABPurchaseOrder purchaseOrder)
    {
        int requestCode = actorPurchaser.registerPurchaseFinishedListener(purchaseFinishedListener);
        actorPurchaser.launchPurchaseSequence(requestCode, purchaseOrder);
    }

    protected void popDialogLoadingInfo()
    {
        progressDialog = ProgressDialog.show(
                getActivity(),
                Application.getResourceString(R.string.store_billing_loading_info_window_title),
                Application.getResourceString(R.string.store_billing_loading_info_window_message),
                true,
                true,
                new DialogInterface.OnCancelListener()
                {
                    @Override public void onCancel(DialogInterface dialog)
                    {
                        runOnShowSkuDetailsMilestoneComplete = null;
                    }
                });
    }

    protected void popFailedToLoadRequiredInfo()
    {
        IABAlertDialogUtil.popFailedToLoadRequiredInfo(getActivity());
    }

    protected void launchReportPurchaseSequence(BaseIABPurchase purchase)
    {
        launchReportPurchaseSequence(getBillingActor(), purchase);
    }

    protected void launchReportPurchaseSequence(THIABActorPurchaseReporter actorPurchaseReporter, BaseIABPurchase purchase)
    {
        progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.store_billing_report_api_launching_window_title),
                getString(R.string.store_billing_report_api_launching_window_message),
                true);
        int requestCode = actorPurchaseReporter.registerPurchaseReportedHandler(purchaseReportedListener);
        actorPurchaseReporter.launchReportSequence(requestCode, purchase);
    }

    protected void handlePurchaseReportSuccess(BaseIABPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
    {
        userProfileCache.get().put(updatedUserProfile.getBaseKey(), updatedUserProfile);
        launchConsumeSequence(reportedPurchase);
    }

    protected void launchConsumeSequence(BaseIABPurchase reportedPurchase)
    {
        launchConsumeSequence(getBillingActor(), reportedPurchase);
    }

    protected void launchConsumeSequence(THIABActorPurchaseConsumer actorConsumer, BaseIABPurchase reportedPurchase)
    {
        int requestCode = actorConsumer.registerConsumeFinishedListener(consumptionFinishedListener);
        actorConsumer.launchConsumeSequence(requestCode, reportedPurchase);
    }

    protected void handlePurchaseConsumed(BaseIABPurchase purchase)
    {
        ProgressDialog dialog = progressDialog;
        if (dialog != null)
        {
            dialog.setTitle(R.string.store_billing_report_api_finishing_window_title);
            dialog.setMessage(getString(R.string.store_billing_report_api_finishing_window_title));
        }

        getView().postDelayed(new Runnable()
        {
            @Override public void run()
            {
                ProgressDialog dialog = progressDialog;
                if (dialog != null)
                {
                    dialog.hide();
                }

                Runnable runOnPurchaseCompleteCopy = runOnPurchaseComplete;
                runOnPurchaseComplete = null;
                if (runOnPurchaseCompleteCopy != null)
                {
                    runOnPurchaseCompleteCopy.run();
                }
            }
        }, 1500);
    }

    protected void handlePurchaseConsumeFailed()
    {

    }
}
