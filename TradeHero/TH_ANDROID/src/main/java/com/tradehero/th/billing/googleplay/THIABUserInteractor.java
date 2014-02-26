package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.OnBillingAvailableListener;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABItemAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.common.billing.googleplay.exception.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exception.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exception.IABVerificationFailedException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.ShowProductDetailsMilestone;
import com.tradehero.th.billing.THBaseBillingInteractor;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.fragments.billing.StoreSKUDetailView;
import com.tradehero.th.fragments.billing.googleplay.THSKUDetailsAdapter;
import com.tradehero.th.fragments.social.hero.FollowHeroCallback;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * It expects its Activity to implement THIABInteractor.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
public class THIABUserInteractor
    extends
        THBaseBillingInteractor<
        IABSKUListType,
        IABSKU,
        THIABProductDetail,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        THIABLogicHolder,
        THIABProductDetailDomainInformer,
        StoreSKUDetailView,
        THSKUDetailsAdapter,
        BillingPurchaser.OnPurchaseFinishedListener<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                IABException>,
        THIABPurchaserHolder,
        PurchaseReporter.OnPurchaseReportedListener<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                IABException>,
        THIABPurchaseReporterHolder,
        IABException>
    implements THIABInteractor
{
    public static final String BUNDLE_KEY_ACTION = THIABUserInteractor.class.getName() + ".action";
    public static final int ACTION_RESET_PORTFOLIO = 1;

    @Inject Lazy<THIABProductDetailCache> thiabProductDetailCache;
    @Inject protected THIABLogicHolder billingActor;
    protected THIABPurchaseRestorer purchaseRestorer;
    protected BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> inventoryFetchedForgetListener;

    @Inject protected IABAlertDialogUtil iabAlertDialogUtil;
    @Inject protected IABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject protected UserProfileDTOUtil userProfileDTOUtil;

    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        IABException> consumptionFinishedListener;
    protected THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener purchaseRestorerFinishedListener;

    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<UserService> userService;
    protected Callback<UserProfileDTO> followCallback;

    //<editor-fold desc="Constructors">
    public THIABUserInteractor()
    {
        super();
        purchaseRestorer = new THIABPurchaseRestorer(billingActor);
        showProductDetailsMilestoneListener = new Milestone.OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                handleShowProductDetailsMilestoneComplete();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                Timber.e("Failed to complete ShowSkuDetailsMilestone", throwable);
                handleShowProductDetailsMilestoneFailed(throwable);
            }
        };

        purchaseRestorer.setPurchaseRestoreFinishedListener(purchaseRestorerFinishedListener);
        purchaseRestorer.init();
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    protected void prepareCallbacks(final CurrentActivityHolder activityHolder)
    {
        super.prepareCallbacks(activityHolder);

        if (consumptionFinishedListener == null)
        {
            consumptionFinishedListener = new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
            {
                @Override public void onPurchaseConsumeFailed(int requestCode, THIABPurchase purchase, IABException exception)
                {
                    haveLogicHolderForget(requestCode);
                    runOnPurchaseComplete = null;
                    Timber.e("Failed to consume purchase", exception);
                    if (progressDialog != null)
                    {
                        progressDialog.hide();
                    }
                    iabAlertDialogUtil.popOfferSendEmailSupportConsumeFailed(activityHolder.getCurrentContext(), exception);
                }

                @Override public void onPurchaseConsumed(int requestCode, THIABPurchase purchase)
                {
                    haveLogicHolderForget(requestCode);
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

                    IABPurchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                            activityHolder.getCurrentContext(),
                            consumed,
                            reportFailed,
                            consumeFailed,
                            IABPurchaseRestorerAlertUtil.createFailedRestoreClickListener(activityHolder.getCurrentActivity(), new Exception()),
                            true); // TODO have a better exception
                }

                @Override public void onPurchaseRestoreFinished(List<THIABPurchase> consumed, List<THIABPurchase> consumeFailed)
                {
                    Timber.d("onPurchaseRestoreFinished2");
                }

                @Override public void onPurchaseRestoreFailed(IABException iabException)
                {
                    Timber.e("onPurchaseRestoreFailed", iabException);
                    if (iabException instanceof Exception)
                    {
                        IABPurchaseRestorerAlertUtil.popSendEmailSupportRestoreFailed(activityHolder.getCurrentContext(), (Exception) iabException);
                    }
                }
            };
        }

        if (followCallback == null)
        {
            createFollowCallback();
        }
    }

    @Override protected BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException> createPurchaseFinishedListener()
    {
        return new THIABUserInteractorOnPurchaseFinishedListener(currentActivityHolder);
    }

    @Override protected PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> createPurchaseReportedListener()
    {
        return new THIABUserInteractorOnPurchaseReportedListener(currentActivityHolder);
    }

    public void onPause()
    {
        inventoryFetchedForgetListener = null;
        super.onPause();
    }

    public void onDestroy()
    {
        purchaseReportedListener = null;
        consumptionFinishedListener = null;
        purchaseRestorerFinishedListener = null;
        if (purchaseRestorer != null)
        {
            purchaseRestorer.setPurchaseRestoreFinishedListener(null);
        }
        purchaseRestorer = null;
        followCallback = null;
        billingActor = null;
        super.onDestroy();
    }
    //</editor-fold>

    //<editor-fold desc="Logic Holder Handling">
    @Override public THIABLogicHolder getBillingLogicHolder()
    {
        return billingActor;
    }

    @Override public THIABLogicHolder getTHBillingLogicHolder()
    {
        return billingActor;
    }
    //</editor-fold>


    @Override protected BillingAlertDialogUtil<IABSKU, THIABProductDetail, THIABProductDetailDomainInformer, StoreSKUDetailView, THSKUDetailsAdapter> getBillingAlertDialogUtil()
    {
        return iabAlertDialogUtil;
    }

    //<editor-fold desc="Inventory Preparation">
    protected void prepareProductDetailsPrerequisites()
    {
        prepareProductDetailsPrerequisites(IABSKUListType.getInApp());
    }

    @Override protected ShowProductDetailsMilestone createShowProductDetailsMilestone(
            IABSKUListType iabskuListType)
    {
        return new ShowSkuDetailsMilestone(iabskuListType, currentUserId.toUserBaseKey());
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Virtual Dollars">
    @Override public void purchaseVirtualDollar(OwnedPortfolioId ownedPortfolioId)
    {
        throw new IllegalStateException("Not implemented");
    }

    @Override protected OnBillingAvailableListener<IABException> createPurchaseVirtualDollarWhenAvailableListener(OwnedPortfolioId ownedPortfolioId)
    {
        return new THIABUserInteractorPurchaseVirtualDollarWhenAvailableListener(ownedPortfolioId);
    }

    protected class THIABUserInteractorPurchaseVirtualDollarWhenAvailableListener extends THBaseBillingInteractorPurchaseVirtualDollarWhenAvailableListener
    {
        public THIABUserInteractorPurchaseVirtualDollarWhenAvailableListener(OwnedPortfolioId portfolioId)
        {
            super(portfolioId);
        }

        @Override public void onBillingAvailable()
        {
            // TODO wait for inventory
        }
    }
    //</editor-fold>



    protected void createFollowCallback()
    {
        followCallback = new UserInteractorFollowHeroCallback(heroListCache.get(), userProfileCache.get());
    }

    protected boolean hadErrorLoadingInventory()
    {
        THIABLogicHolder billingActorCopy = this.billingActor;
        return billingActorCopy != null && billingActorCopy.getInventoryFetcherHolder().hadErrorLoadingInventory();
    }

    //<editor-fold desc="THIABInteractor">
    //</editor-fold>

    public AlertDialog popErrorConditional()
    {
        Boolean billingAvailable = isBillingAvailable();
        if (billingAvailable == null || !billingAvailable) // TODO wait when is null
        {
            return popBillingUnavailable();
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
                        int requestCode = getBillingLogicHolder().getUnusedRequestCode();
                        getBillingLogicHolder().getInventoryFetcherHolder().registerInventoryFetchedListener(requestCode, inventoryFetchedForgetListener);
                        getBillingLogicHolder().getInventoryFetcherHolder().launchInventoryFetchSequence(requestCode, new ArrayList<IABSKU>());
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
                getBillingLogicHolder().forgetRequestCode(requestCode);
            }

            @Override public void onInventoryFetchFail(int requestCode, List<IABSKU> productIdentifiers, IABException exception)
            {
                getBillingLogicHolder().forgetRequestCode(requestCode);
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

    public void popBuyVirtualDollars(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THBillingInteractor.DOMAIN_VIRTUAL_DOLLAR, R.string.store_buy_virtual_dollar_window_title, runOnPurchaseComplete);
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

    public void popBuyFollowCredits(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THBillingInteractor.DOMAIN_FOLLOW_CREDITS, R.string.store_buy_follow_credits_window_message, runOnPurchaseComplete);
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

    public void popBuyStockAlerts(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THBillingInteractor.DOMAIN_STOCK_ALERTS, R.string.store_buy_stock_alerts_window_title, runOnPurchaseComplete);
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

    public void popBuyResetPortfolio(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THBillingInteractor.DOMAIN_RESET_PORTFOLIO, R.string.store_buy_reset_portfolio_window_title, runOnPurchaseComplete);
    }

    //</editor-fold>

    //<editor-fold desc="Purchasing Sequence">
    @Override protected void launchPurchaseSequence(IABSKU productIdentifier)
    {
        launchPurchaseSequence(new THIABPurchaseOrder(productIdentifier, applicablePortfolioId));
    }

    @Override protected void launchPurchaseSequence(THIABPurchaseOrder purchaseOrder)
    {
        launchPurchaseSequence(getBillingLogicHolder().getPurchaserHolder(), purchaseOrder);
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Reporting Sequence">
    @Override protected void launchReportPurchaseSequence(THIABPurchase purchase)
    {
        launchReportPurchaseSequence(getBillingLogicHolder().getPurchaseReporterHolder(), purchase);
    }

    protected void handlePurchaseReportSuccess(THIABPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
    {
        super.handlePurchaseReportSuccess(reportedPurchase, updatedUserProfile);
        launchConsumeSequence(reportedPurchase);
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Consumption Sequence">
    protected void launchConsumeSequence(THIABPurchase reportedPurchase)
    {
        launchConsumeSequence(getBillingLogicHolder().getPurchaseConsumerHolder(), reportedPurchase);
    }

    protected void launchConsumeSequence(THIABPurchaseConsumerHolder consumerHolder, THIABPurchase reportedPurchase)
    {
        int requestCode = getBillingLogicHolder().getUnusedRequestCode();
        consumerHolder.registerConsumeFinishedListener(requestCode, consumptionFinishedListener);
        consumerHolder.launchConsumeSequence(requestCode, reportedPurchase);
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
    //</editor-fold>

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
                            runOnShowProductDetailsMilestoneComplete = null;
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
                progressDialog.setCanceledOnTouchOutside(true);
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

    public void doAction(int action)
    {
        switch (action)
        {
            case ACTION_RESET_PORTFOLIO:
                conditionalPopBuyResetPortfolio();
                break;
        }
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

    protected class THIABUserInteractorOnPurchaseFinishedListener extends THBaseBillingInteractorOnPurchaseFinishedListener
    {
        public THIABUserInteractorOnPurchaseFinishedListener(CurrentActivityHolder activityHolder)
        {
            super(activityHolder);
        }

        @Override public void onPurchaseFinished(int requestCode, THIABPurchaseOrder purchaseOrder, THIABPurchase purchase)
        {
            super.onPurchaseFinished(requestCode, purchaseOrder, purchase);
            launchReportPurchaseSequence(purchase);
        }

        @Override public void onPurchaseFailed(int requestCode, THIABPurchaseOrder purchaseOrder, IABException exception)
        {
            super.onPurchaseFailed(requestCode, purchaseOrder, exception);
            if (exception instanceof IABVerificationFailedException)
            {
                iabAlertDialogUtil.popVerificationFailed(activityHolder.getCurrentActivity());
            }
            else if (exception instanceof IABUserCancelledException)
            {
                iabAlertDialogUtil.popUserCancelled(activityHolder.getCurrentActivity());
            }
            else if (exception instanceof IABBadResponseException)
            {
                iabAlertDialogUtil.popBadResponse(activityHolder.getCurrentActivity());
            }
            else if (exception instanceof IABRemoteException)
            {
                iabAlertDialogUtil.popRemoteError(activityHolder.getCurrentActivity());
            }
            else if (exception instanceof IABItemAlreadyOwnedException)
            {
                iabAlertDialogUtil.popSKUAlreadyOwned(activityHolder.getCurrentActivity(),
                        thiabProductDetailCache.get()
                                .get(purchaseOrder.getProductIdentifier()));
            }
            else if (exception instanceof IABSendIntentException)
            {
                iabAlertDialogUtil.popSendIntent(activityHolder.getCurrentActivity());
            }
            else
            {
                iabAlertDialogUtil.popUnknownError(activityHolder.getCurrentActivity());
            }
        }
    }

    protected class THIABUserInteractorOnPurchaseReportedListener extends THBaseBillingInteractorOnPurchaseReportedListener
    {
        public THIABUserInteractorOnPurchaseReportedListener(CurrentActivityHolder activityHolder)
        {
            super(activityHolder);
        }
    }
}
