package com.tradehero.th.fragments.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
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
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.fragments.social.hero.FollowHeroCallback;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * It expects its Activity to implement THIABActorUser.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
public class THIABUserInteractor
        implements IABAlertDialogUtil.OnDialogSKUDetailsClickListener<THIABProductDetail>,
        THIABActorUser
{
    public static final String TAG = THIABUserInteractor.class.getSimpleName();

    protected WeakReference<Activity> activityWeak = new WeakReference<>(null);
    protected WeakReference<Handler> handlerWeak = new WeakReference<>(null);

    private ShowSkuDetailsMilestone showSkuDetailsMilestone;
    private Milestone.OnCompleteListener showSkuDetailsMilestoneListener;
    private Runnable runOnShowSkuDetailsMilestoneComplete;
    protected Throwable showSkuDetailsMilestoneException;

    private ProgressDialog progressDialog;
    @Inject Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<THIABProductDetailCache> thiabProductDetailCache;
    protected WeakReference<THIABActor> billingActor = new WeakReference<>(null);
    protected InventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> inventoryFetchedForgetListener;
    protected OwnedPortfolioId applicablePortfolioId;
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

    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<UserService> userService;
    protected Callback<UserProfileDTO> followCallback;
    private UserProfileDTO userProfileDTO;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserProfileDTO> userProfileFetchTask;

    /**
     * The activityWeak and handler should be strongly referenced elsewhere
     * @param activity
     */
    public THIABUserInteractor(Activity activity, THIABActor billingActor, Handler handler)
    {
        super();
        DaggerUtils.inject(this);
        this.activityWeak = new WeakReference<>(activity);
        setBillingActor(billingActor);
        this.handlerWeak = new WeakReference<>(handler);
        showSkuDetailsMilestoneListener = new Milestone.OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                handleShowSkuDetailsMilestoneComplete();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                THLog.e(TAG, "Failed to complete ShowSkuDetailsMilestone", throwable);
                showSkuDetailsMilestoneException = throwable;
                handleShowSkuDetailsMilestoneFailed(throwable);
            }
        };
        prepareCallbacks(activity);
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
            purchaseFinishedListener = new BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, BaseIABPurchase, IABException>()
            {
                @Override public void onPurchaseFailed(int requestCode, THIABPurchaseOrder purchaseOrder, IABException exception)
                {
                    haveActorForget(requestCode);
                    runOnPurchaseComplete = null;
                    THLog.e(TAG, "onPurchaseFailed requestCode " + requestCode, exception);
                    if (exception instanceof IABVerificationFailedException)
                    {
                        IABAlertDialogUtil.popVerificationFailed(context);
                    }
                    else if (exception instanceof IABUserCancelledException)
                    {
                        IABAlertDialogUtil.popUserCancelled(context);
                    }
                    else if (exception instanceof IABBadResponseException)
                    {
                        IABAlertDialogUtil.popBadResponse(context);
                    }
                    else if (exception instanceof IABRemoteException)
                    {
                        IABAlertDialogUtil.popRemoteError(context);
                    }
                    else if (exception instanceof IABAlreadyOwnedException)
                    {
                        IABAlertDialogUtil.popSKUAlreadyOwned(context, thiabProductDetailCache.get().get(purchaseOrder.getProductIdentifier()));
                    }
                    else if (exception instanceof IABSendIntentException)
                    {
                        IABAlertDialogUtil.popSendIntent(context);
                    }
                    else
                    {
                        IABAlertDialogUtil.popUnknownError(context);
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
                    IABAlertDialogUtil.popFailedToReport(context);
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
                    IABAlertDialogUtil.popOfferSendEmailSupportConsumeFailed(context, exception);
                }

                @Override public void onPurchaseConsumed(int requestCode, BaseIABPurchase purchase)
                {
                    haveActorForget(requestCode);
                    handlePurchaseConsumed(purchase);
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
        THIABActor actor = getBillingActor();
        if (actor != null)
        {
            actor.forgetRequestCode(requestCode);
        }
    }

    protected void preparePrerequisites()
    {
        if (this.applicablePortfolioId == null)
        {
            this.applicablePortfolioId = new OwnedPortfolioId(currentUserBaseKeyHolder.get().getCurrentUserBaseKey().key, null);
        }
        if (this.applicablePortfolioId.userId == null)
        {
            this.applicablePortfolioId = new OwnedPortfolioId(currentUserBaseKeyHolder.get().getCurrentUserBaseKey().key, this.applicablePortfolioId.portfolioId);
        }

        if (this.applicablePortfolioId.portfolioId == null)
        {
            final OwnedPortfolioId ownedPortfolioId = portfolioCompactListCache.get().getDefaultPortfolio(this.applicablePortfolioId.getUserBaseKey());
            if (ownedPortfolioId != null && ownedPortfolioId.portfolioId != null)
            {
                this.applicablePortfolioId = ownedPortfolioId;
            }
        }

        showSkuDetailsMilestone = new ShowSkuDetailsMilestone(activityWeak.get(), getBillingActor(), IABSKUListType.getInApp(), this.applicablePortfolioId.getUserBaseKey());
        showSkuDetailsMilestone.setOnCompleteListener(showSkuDetailsMilestoneListener);
        showSkuDetailsMilestoneException = null;
        showSkuDetailsMilestone.launch();
    }

    protected void handleShowSkuDetailsMilestoneFailed(Throwable throwable)
    {
        // Nothing to do unless overridden
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
        }
    }

    protected boolean isBillingAvailable()
    {
        THIABActor billingActor = getBillingActor();
        return billingActor != null && billingActor.isBillingAvailable();
    }

    protected boolean hadErrorLoadingInventory()
    {
        THIABActor billingActor = getBillingActor();
        return billingActor != null && billingActor.hadErrorLoadingInventory();
    }

    protected boolean isInventoryReady()
    {
        THIABActor billingActor = getBillingActor();
        return billingActor != null && billingActor.isInventoryReady();
    }

    //<editor-fold desc="THIABActorUser">
    public THIABActor getBillingActor()
    {
        return billingActor.get();
    }

    /**
     * The billingActor should be strongly referenced elsewhere
     * @param billingActor
     */
    public void setBillingActor(THIABActor billingActor)
    {
        this.billingActor = new WeakReference<>(billingActor);
    }
    //</editor-fold>

    public AlertDialog conditionalPopBillingNotAvailable()
    {
        if (!isBillingAvailable())
        {
            return IABAlertDialogUtil.popBillingUnavailable(activityWeak.get());
        }
        return null;
    }

    public AlertDialog popErrorConditional()
    {
        if (!isBillingAvailable())
        {
            return IABAlertDialogUtil.popBillingUnavailable(activityWeak.get());
        }
        else if (hadErrorLoadingInventory())
        {
            return popErrorWhenLoading();
        }
        return null;
    }

    public AlertDialog popErrorWhenLoading()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activityWeak.get());
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

    protected InventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> createForgetFetchedListener()
    {
        return new InventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>()
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
                Handler handler = THIABUserInteractor.this.handlerWeak.get();
                if (handler != null)
                {
                    handler.post(new Runnable()
                    {
                        @Override public void run()
                        {
                            IABAlertDialogSKUUtil.popBuyDialog(activityWeak.get(), getBillingActor(), THIABUserInteractor.this, skuDomain, titleResId,
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
        Activity activity = this.activityWeak.get();
        if (activity != null)
        {
            progressDialog = ProgressDialog.show(
                    activity,
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
    }

    protected void popFailedToLoadRequiredInfo()
    {
        IABAlertDialogUtil.popFailedToLoadRequiredInfo(activityWeak.get());
    }

    protected void launchReportPurchaseSequence(BaseIABPurchase purchase)
    {
        launchReportPurchaseSequence(getBillingActor(), purchase);
    }

    protected void launchReportPurchaseSequence(THIABActorPurchaseReporter actorPurchaseReporter, BaseIABPurchase purchase)
    {
        Activity activity = this.activityWeak.get();
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

    protected void handlePurchaseReportSuccess(BaseIABPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
    {
        userProfileDTO = updatedUserProfile;
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
            Activity activity = this.activityWeak.get();
            if (activity != null)
            {
                dialog.setMessage(activity.getString(R.string.store_billing_report_api_finishing_window_title));
            }
        }

        Handler handler = handlerWeak.get();
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
            THLog.w(TAG, "Handler is null");
        }
    }

    protected void handlePurchaseConsumeFailed()
    {
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
            Activity activity = activityWeak.get();
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
        Activity activity = activityWeak.get();
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
            THLog.e(TAG, "Failed to un/follow", error);
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            THToast.show(R.string.manage_heroes_follow_failed);
        }
    }
}
