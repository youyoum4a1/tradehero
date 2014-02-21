package com.tradehero.th.fragments.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exception.IABItemAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.common.billing.googleplay.exception.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exception.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exception.IABVerificationFailedException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.IABAlertDialogSKUUtil;
import com.tradehero.th.billing.googleplay.IABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THIABActorPurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABActorPurchaser;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.googleplay.THIABPurchaseRestorer;
import com.tradehero.th.fragments.social.hero.FollowHeroCallback;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * It expects its Activity to implement THIABActorUser.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
public class THIABUserInteractor
        implements IABAlertDialogUtil.OnDialogSKUDetailsClickListener<THIABProductDetail>,
        THIABActorUser
{
    @Inject protected CurrentActivityHolder currentActivityHolder;

    private ShowSkuDetailsMilestone showSkuDetailsMilestone;
    private Milestone.OnCompleteListener showSkuDetailsMilestoneListener;
    private Runnable runOnShowSkuDetailsMilestoneComplete;
    protected Throwable showSkuDetailsMilestoneException;

    private ProgressDialog progressDialog;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<THIABProductDetailCache> thiabProductDetailCache;
    @Inject protected THIABActor billingActor;
    protected THIABPurchaseRestorer purchaseRestorer;
    protected BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> inventoryFetchedForgetListener;
    protected OwnedPortfolioId applicablePortfolioId;
    private Runnable runOnPurchaseComplete;

    @Inject protected IABAlertDialogSKUUtil iabAlertDialogSKUUtil;
    @Inject protected PurchaseRestorerAlertUtil purchaseRestorerAlertUtil;
    @Inject protected UserProfileDTOUtil userProfileDTOUtil;

    protected BillingPurchaser.OnPurchaseFinishedListener<
        IABSKU,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        IABException> purchaseFinishedListener;
    protected PurchaseReporter.OnPurchaseReportedListener<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        Exception> purchaseReportedListener;
    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        IABException> consumptionFinishedListener;
    protected THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener purchaseRestorerFinishedListener;

    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<UserService> userService;
    protected Callback<UserProfileDTO> followCallback;
    private UserProfileDTO userProfileDTO;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;

    public THIABUserInteractor()
    {
        super();
        DaggerUtils.inject(this);
        purchaseRestorer = new THIABPurchaseRestorer(currentActivityHolder.getCurrentActivity(),
                billingActor,
                billingActor,
                billingActor,
                billingActor);
        showSkuDetailsMilestoneListener = new Milestone.OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                handleShowSkuDetailsMilestoneComplete();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                Timber.e("Failed to complete ShowSkuDetailsMilestone", throwable);
                showSkuDetailsMilestoneException = throwable;
                handleShowSkuDetailsMilestoneFailed(throwable);
            }
        };
        prepareCallbacks(currentActivityHolder.getCurrentActivity());

        purchaseRestorer.setFinishedListener(purchaseRestorerFinishedListener);
        purchaseRestorer.init();
    }

    public void onPause()
    {
        inventoryFetchedForgetListener = null;
        runOnShowSkuDetailsMilestoneComplete = null;
    }

    public void onDestroy()
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
            progressDialog = null;
        }
        purchaseFinishedListener = null;
        purchaseReportedListener = null;
        consumptionFinishedListener = null;
        showSkuDetailsMilestoneListener = null;
        purchaseRestorerFinishedListener = null;
        if (purchaseRestorer != null)
        {
            purchaseRestorer.setFinishedListener(null);
        }
        purchaseRestorer = null;
        followCallback = null;
    }

    public void setApplicablePortfolioId(OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
        preparePrerequisites();
    }

    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return applicablePortfolioId;
    }

    private void prepareCallbacks(final Context context)
    {
        if (purchaseFinishedListener == null)
        {
            purchaseFinishedListener = new BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException>()
            {
                @Override public void onPurchaseFailed(int requestCode, THIABPurchaseOrder purchaseOrder, IABException exception)
                {
                    haveActorForget(requestCode);
                    runOnPurchaseComplete = null;
                    Timber.e("onPurchaseFailed requestCode %d", requestCode, exception);
                    if (exception instanceof IABVerificationFailedException)
                    {
                        iabAlertDialogSKUUtil.popVerificationFailed(context);
                    }
                    else if (exception instanceof IABUserCancelledException)
                    {
                        iabAlertDialogSKUUtil.popUserCancelled(context);
                    }
                    else if (exception instanceof IABBadResponseException)
                    {
                        iabAlertDialogSKUUtil.popBadResponse(context);
                    }
                    else if (exception instanceof IABRemoteException)
                    {
                        iabAlertDialogSKUUtil.popRemoteError(context);
                    }
                    else if (exception instanceof IABItemAlreadyOwnedException)
                    {
                        iabAlertDialogSKUUtil.popSKUAlreadyOwned(context, thiabProductDetailCache.get().get(purchaseOrder.getProductIdentifier()));
                    }
                    else if (exception instanceof IABSendIntentException)
                    {
                        iabAlertDialogSKUUtil.popSendIntent(context);
                    }
                    else
                    {
                        iabAlertDialogSKUUtil.popUnknownError(context);
                    }
                }

                @Override public void onPurchaseFinished(int requestCode, THIABPurchaseOrder purchaseOrder, THIABPurchase purchase)
                {
                    haveActorForget(requestCode);
                    launchReportPurchaseSequence(purchase);
                }
            };
        }

        if (purchaseReportedListener == null)
        {
            purchaseReportedListener = new PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception>()
            {
                @Override public void onPurchaseReported(int requestCode, THIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
                {
                    haveActorForget(requestCode);
                    handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
                }

                @Override public void onPurchaseReportFailed(int requestCode, THIABPurchase reportedPurchase, Exception error)
                {
                    haveActorForget(requestCode);
                    runOnPurchaseComplete = null;
                    Timber.e("Failed to report to server", error);
                    if (progressDialog != null)
                    {
                        progressDialog.hide();
                    }
                    iabAlertDialogSKUUtil.popFailedToReport(context);
                }
            };
        }

        if (consumptionFinishedListener == null)
        {
            consumptionFinishedListener = new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
            {
                @Override public void onPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
                {
                    haveActorForget(requestCode);
                    runOnPurchaseComplete = null;
                    Timber.e("Failed to consume purchase", exception);
                    if (progressDialog != null)
                    {
                        progressDialog.hide();
                    }
                    iabAlertDialogSKUUtil.popOfferSendEmailSupportConsumeFailed(context, exception);
                }

                @Override public void onPurchaseConsumed(int requestCode, THIABPurchase purchase)
                {
                    haveActorForget(requestCode);
                    handlePurchaseConsumed(purchase);
                }
            };
        }

        if (purchaseRestorerFinishedListener == null)
        {
            purchaseRestorerFinishedListener = new THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener()
            {
                @Override
                public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed)
                {
                    Timber.d("onPurchaseRestoreFinished3");
                    if (progressDialog != null)
                    {
                        progressDialog.hide();
                    }

                    purchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                            context,
                            consumed,
                            reportFailed,
                            consumeFailed,
                            purchaseRestorerAlertUtil.createFailedRestoreClickListener(context, new Exception()),
                            true); // TODO have a better exception
                }

                @Override public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> consumeFailed)
                {
                    Timber.d("onPurchaseRestoreFinished2");
                }

                @Override public void onPurchaseRestoreFailed(Throwable throwable)
                {
                    Timber.e("onPurchaseRestoreFailed", throwable);
                    if (throwable instanceof Exception)
                    {
                        purchaseRestorerAlertUtil.popSendEmailSupportRestoreFailed(context, (Exception) throwable);
                    }
                }
            };
        }

        if (followCallback == null)
        {
            createFollowCallback();
        }
    }

    protected void createFollowCallback()
    {
        followCallback = new UserInteractorFollowHeroCallback(heroListCache.get(), userProfileCache.get());
    }

    protected void haveActorForget(int requestCode)
    {
        THIABActor actor = this.billingActor;
        if (actor != null)
        {
            actor.forgetRequestCode(requestCode);
        }
    }

    protected void preparePrerequisites()
    {
        if (this.applicablePortfolioId == null)
        {
            this.applicablePortfolioId = new OwnedPortfolioId(currentUserId.get(), null);
        }
        if (this.applicablePortfolioId.userId == null)
        {
            this.applicablePortfolioId = new OwnedPortfolioId(currentUserId.get(), this.applicablePortfolioId.portfolioId);
        }

        if (this.applicablePortfolioId.portfolioId == null)
        {
            final OwnedPortfolioId ownedPortfolioId = portfolioCompactListCache.get().getDefaultPortfolio(this.applicablePortfolioId.getUserBaseKey());
            if (ownedPortfolioId != null && ownedPortfolioId.portfolioId != null)
            {
                this.applicablePortfolioId = ownedPortfolioId;
            }
        }

        showSkuDetailsMilestone = new ShowSkuDetailsMilestone(
                currentActivityHolder.getCurrentActivity(),
                billingActor,
                IABSKUListType.getInApp(),
                this.applicablePortfolioId.getUserBaseKey());
        showSkuDetailsMilestone.setOnCompleteListener(showSkuDetailsMilestoneListener);
        showSkuDetailsMilestoneException = null;
        showSkuDetailsMilestone.launch();
    }

    protected void handleShowSkuDetailsMilestoneFailed(Throwable throwable)
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    protected void handleShowSkuDetailsMilestoneComplete()
    {
        // At this stage, we know the applicable portfolio is available in the cache
        if (this.applicablePortfolioId.portfolioId == null)
        {
            this.applicablePortfolioId = portfolioCompactListCache.get().getDefaultPortfolio(this.applicablePortfolioId.getUserBaseKey());
        }
        // We also know that the userProfile is in the cache
        this.userProfileDTO = userProfileCache.get().get(this.applicablePortfolioId.getUserBaseKey());

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

    public void waitForSkuDetailsMilestoneComplete(Runnable runnable)
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
            else
            {
                Timber.d("showSkuDetailsMilestone is already running");
            }
        }
    }

    protected boolean isBillingAvailable()
    {
        THIABActor billingActorCopy = this.billingActor;
        return billingActorCopy != null && billingActorCopy.isBillingAvailable();
    }

    protected boolean hadErrorLoadingInventory()
    {
        THIABActor billingActorCopy = this.billingActor;
        return billingActorCopy != null && billingActorCopy.hadErrorLoadingInventory();
    }

    protected boolean isInventoryReady()
    {
        THIABActor billingActorCopy = this.billingActor;
        return billingActorCopy != null && billingActorCopy.isInventoryReady();
    }

    //<editor-fold desc="THIABActorUser">
    public THIABActor getBillingActor()
    {
        return billingActor;
    }

    /**
     * The billingActor should be strongly referenced elsewhere
     * @param billingActor
     */
    public void setBillingActor(THIABActor billingActor)
    {
        throw new IllegalStateException("You cannot change the billing Actor");
    }
    //</editor-fold>

    public AlertDialog conditionalPopBillingNotAvailable()
    {
        if (!isBillingAvailable())
        {
            return iabAlertDialogSKUUtil.popBillingUnavailable(currentActivityHolder.getCurrentActivity());
        }
        return null;
    }

    public AlertDialog popErrorConditional()
    {
        if (!isBillingAvailable())
        {
            return iabAlertDialogSKUUtil.popBillingUnavailable(currentActivityHolder.getCurrentActivity());
        }
        else if (hadErrorLoadingInventory())
        {
            return popErrorWhenLoading();
        }
        return null;
    }

    public AlertDialog popErrorWhenLoading()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentActivityHolder.getCurrentActivity());
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
        return alertDialog;
    }

    protected BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> createForgetFetchedListener()
    {
        return new BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<IABSKU> productIdentifiers, Map<IABSKU, THIABProductDetail> inventory)
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
    public void conditionalPopBuyVirtualDollars()
    {
        conditionalPopBuyVirtualDollars(null);
    }

    public void conditionalPopBuyVirtualDollars(Runnable runOnPurchaseComplete)
    {
        if (popErrorConditional() == null)
        {
            popBuyVirtualDollars(runOnPurchaseComplete);
        }
    }

    public void popBuyVirtualDollars()
    {
        popBuyVirtualDollars(null);
    }

    public void popBuyVirtualDollars(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetail.DOMAIN_VIRTUAL_DOLLAR, R.string.store_buy_virtual_dollar_window_title, runOnPurchaseComplete);
    }

    public void conditionalPopBuyFollowCredits()
    {
        conditionalPopBuyFollowCredits(null);
    }

    public void conditionalPopBuyFollowCredits(Runnable runOnPurchaseComplete)
    {
        if (popErrorConditional() == null)
        {
            popBuyFollowCredits(runOnPurchaseComplete);
        }
    }

    public void popBuyFollowCredits()
    {
        popBuyFollowCredits(null);
    }

    public void popBuyFollowCredits(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetail.DOMAIN_FOLLOW_CREDITS, R.string.store_buy_follow_credits_window_message, runOnPurchaseComplete);
    }

    public void conditionalPopBuyStockAlerts()
    {
        conditionalPopBuyStockAlerts(null);
    }

    public void conditionalPopBuyStockAlerts(Runnable runOnPurchaseComplete)
    {
        if (popErrorConditional() == null)
        {
            popBuyStockAlerts(runOnPurchaseComplete);
        }
    }

    public void popBuyStockAlerts()
    {
        popBuyStockAlerts(null);
    }

    public void popBuyStockAlerts(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetail.DOMAIN_STOCK_ALERTS, R.string.store_buy_stock_alerts_window_title, runOnPurchaseComplete);
    }

    public void conditionalPopBuyResetPortfolio()
    {
        conditionalPopBuyResetPortfolio(null);
    }

    public void conditionalPopBuyResetPortfolio(Runnable runOnPurchaseComplete)
    {
        if (popErrorConditional() == null)
        {
            popBuyResetPortfolio(runOnPurchaseComplete);
        }
    }

    public void popBuyResetPortfolio()
    {
        popBuyResetPortfolio(null);
    }

    public void popBuyResetPortfolio(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THIABProductDetail.DOMAIN_RESET_PORTFOLIO, R.string.store_buy_reset_portfolio_window_title, runOnPurchaseComplete);
    }

    public void popBuyDialog(final String skuDomain, final int titleResId)
    {
        popBuyDialog(skuDomain, titleResId, null);
    }

    public void popBuyDialog(final String skuDomain, final int titleResId, final Runnable runOnPurchaseComplete)
    {
        waitForSkuDetailsMilestoneComplete(new Runnable()
        {
            @Override public void run()
            {
                Handler handler = THIABUserInteractor.this.currentActivityHolder.getCurrentHandler();
                Timber.d("handler %s", handler);
                if (handler != null)
                {
                    handler.post(new Runnable()
                    {
                        @Override public void run()
                        {
                            iabAlertDialogSKUUtil.popBuyDialog(
                                    currentActivityHolder.getCurrentActivity(),
                                    getBillingActor(),
                                    THIABUserInteractor.this,
                                    skuDomain,
                                    titleResId,
                                    runOnPurchaseComplete);
                        }
                    });
                }
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="IABAlertDialogUtil.OnDialogSKUDetailsClickListener">
    @Override public void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, THIABProductDetail skuDetails, Runnable runOnPurchaseComplete)
    {
        this.runOnPurchaseComplete = runOnPurchaseComplete;
        launchPurchaseSequence(new THIABPurchaseOrder(skuDetails.getProductIdentifier(), applicablePortfolioId));
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
        Activity activity = this.currentActivityHolder.getCurrentActivity();
        if (activity != null)
        {
            progressDialog = ProgressDialogUtil.show(
                    activity,
                    R.string.store_billing_loading_info_window_title,
                    R.string.store_billing_loading_info_window_message
            );
            progressDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener()
                    {
                        @Override public void onCancel(DialogInterface dialog)
                        {
                            runOnShowSkuDetailsMilestoneComplete = null;
                        }
                    });
        }
    }

    protected void popFailedToLoadRequiredInfo()
    {
        iabAlertDialogSKUUtil.popFailedToLoadRequiredInfo(currentActivityHolder.getCurrentActivity());
    }

    protected void launchReportPurchaseSequence(THIABPurchase purchase)
    {
        launchReportPurchaseSequence(getBillingActor(), purchase);
    }

    protected void launchReportPurchaseSequence(THIABActorPurchaseReporter actorPurchaseReporter, THIABPurchase purchase)
    {
        Activity activity = this.currentActivityHolder.getCurrentActivity();
        if (activity != null)
        {
            progressDialog = ProgressDialog.show(
                    activity,
                    activity.getString(R.string.store_billing_report_api_launching_window_title),
                    activity.getString(R.string.store_billing_report_api_launching_window_message),
                    true);
        }
        int requestCode = actorPurchaseReporter.registerPurchaseReportedHandler(purchaseReportedListener);
        actorPurchaseReporter.launchReportSequence(requestCode, purchase);
    }

    protected void handlePurchaseReportSuccess(THIABPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
    {
        userProfileDTO = updatedUserProfile;
        userProfileCache.get().put(updatedUserProfile.getBaseKey(), updatedUserProfile);
        launchConsumeSequence(reportedPurchase);
    }

    protected void launchConsumeSequence(THIABPurchase reportedPurchase)
    {
        launchConsumeSequence(getBillingActor(), reportedPurchase);
    }

    protected void launchConsumeSequence(THIABActorPurchaseConsumer actorConsumer, THIABPurchase reportedPurchase)
    {
        int requestCode = actorConsumer.registerConsumeFinishedListener(consumptionFinishedListener);
        actorConsumer.launchConsumeSequence(requestCode, reportedPurchase);
    }

    protected void handlePurchaseConsumed(THIABPurchase purchase)
    {
        ProgressDialog dialog = progressDialog;
        if (dialog != null)
        {
            dialog.setTitle(R.string.store_billing_report_api_finishing_window_title);
            Activity activity = this.currentActivityHolder.getCurrentActivity();
            if (activity != null)
            {
                dialog.setMessage(activity.getString(R.string.store_billing_report_api_finishing_window_title));
            }
        }

        Handler handler = currentActivityHolder.getCurrentHandler();
        if (handler != null)
        {
            handler.postDelayed(new Runnable()
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
        else
        {
            Timber.w("Handler is null");
        }
    }

    protected void handlePurchaseConsumeFailed()
    {
    }

    public void launchRestoreSequence()
    {
        Activity activity = this.currentActivityHolder.getCurrentActivity();
        if (activity != null)
        {
            progressDialog = ProgressDialog.show(
                    activity,
                    activity.getString(R.string.store_billing_restoring_purchase_window_title),
                    activity.getString(R.string.store_billing_restoring_purchase_window_message),
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
        waitForSkuDetailsMilestoneComplete(new Runnable()
        {
            @Override public void run()
            {
                purchaseRestorer.launchRestorePurchaseSequence();
            }
        });
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
    }

    public void followHero(final UserBaseKey userBaseKey)
    {
        if (userProfileDTO == null)
        {
            waitForSkuDetailsMilestoneComplete(new Runnable()
            {
                @Override public void run()
                {
                    followHero(userBaseKey);
                }
            });
        }
        else if (userProfileDTO.ccBalance == 0)
        {
            waitForSkuDetailsMilestoneComplete(new Runnable()
            {
                @Override public void run()
                {
                    conditionalPopBuyFollowCredits(new Runnable()
                    {
                        @Override public void run()
                        {
                            // At this point, we have already updated the userProfileDTO, and we can only assume that
                            // the credits have properly been given.
                            followHero(userBaseKey);
                        }
                    });
                }
            });
        }
        else
        {
            Activity activity = currentActivityHolder.getCurrentActivity();
            if (activity != null)
            {
                progressDialog = ProgressDialog.show(
                        activity,
                        activity.getString(R.string.manage_heroes_follow_progress_title),
                        activity.getResources().getString(R.string.manage_heroes_follow_progress_message),
                        true,
                        true
                );
            }
            userService.get().follow(userBaseKey.key, followCallback);
        }
    }

    public void unfollowHero(UserBaseKey userBaseKey)
    {
        Activity activity = currentActivityHolder.getCurrentActivity();
        if (activity != null)
        {
            progressDialog = ProgressDialog.show(
                    activity,
                    activity.getString(R.string.manage_heroes_unfollow_progress_title),
                    activity.getString(R.string.manage_heroes_unfollow_progress_message),
                    true,
                    true
            );
        }

        userService.get().unfollow(userBaseKey.key, followCallback);
    }

    protected class UserInteractorFollowHeroCallback extends FollowHeroCallback
    {
        public UserInteractorFollowHeroCallback(HeroListCache heroListCache, UserProfileCache userProfileCache)
        {
            super(heroListCache, userProfileCache);
        }

        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            super.success(userProfileDTO, response);
            linkWith(userProfileDTO);
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.e("Failed to un/follow", error);
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            THToast.show(R.string.manage_heroes_follow_failed);
        }
    }
}
