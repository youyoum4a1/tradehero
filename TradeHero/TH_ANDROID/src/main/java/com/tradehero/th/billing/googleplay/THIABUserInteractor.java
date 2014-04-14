package com.tradehero.th.billing.googleplay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.OnBillingAvailableListener;
import com.tradehero.common.billing.alipay.AlipayActivity;
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
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.ShowProductDetailsMilestone;
import com.tradehero.th.billing.THBaseBillingInteractor;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.fragments.billing.StoreItemAdapter;
import com.tradehero.th.fragments.billing.StoreSKUDetailView;
import com.tradehero.th.fragments.billing.googleplay.THSKUDetailsAdapter;
import com.tradehero.th.fragments.social.hero.FollowHeroCallback;
import com.tradehero.th.models.alert.AlertSlotDTO;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.LocalyticsConstants;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * It expects its Activity to implement THIABInteractor. Created with IntelliJ IDEA. User: xavier
 * Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates.
 */
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
    @Inject THIABLogicHolder billingActor;
    @Inject THIABAlertDialogUtil THIABAlertDialogUtil;
    @Inject THIABPurchaseRestorerAlertUtil IABPurchaseRestorerAlertUtil;
    @Inject UserProfileDTOUtil userProfileDTOUtil;
    @Inject LocalyticsSession localyticsSession;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected Lazy<SecurityAlertCountingHelper> securityAlertCountingHelperLazy;
    @Inject Lazy<CurrentUserId> currentUserIdLazy;

    protected THIABPurchaseRestorer purchaseRestorer;
    protected
    BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>
            inventoryFetchedForgetListener;

    protected IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            IABException> consumptionFinishedListener;
    protected THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener
            purchaseRestorerFinishedListener;

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
    protected void prepareCallbacks()
    {
        super.prepareCallbacks();

        if (consumptionFinishedListener == null)
        {
            consumptionFinishedListener =
                    new IABPurchaseConsumer.OnIABConsumptionFinishedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
                    {
                        @Override
                        public void onPurchaseConsumeFailed(int requestCode, THIABPurchase purchase,
                                IABException exception)
                        {
                            haveLogicHolderForget(requestCode);
                            runOnPurchaseComplete = null;
                            Timber.e("Failed to consume purchase", exception);
                            if (progressDialog != null)
                            {
                                progressDialog.hide();
                            }
                            Context currentContext = currentActivityHolder.getCurrentContext();
                            if (currentContext != null)
                            {
                                THIABAlertDialogUtil.popOfferSendEmailSupportConsumeFailed(
                                        currentContext, exception);
                            }
                        }

                        @Override
                        public void onPurchaseConsumed(int requestCode, THIABPurchase purchase)
                        {
                            haveLogicHolderForget(requestCode);
                            handlePurchaseConsumed(purchase);
                        }
                    };
        }

        if (purchaseRestorerFinishedListener == null)
        {
            purchaseRestorerFinishedListener =
                    new THIABPurchaseRestorer.OnPurchaseRestorerFinishedListener()
                    {
                        @Override
                        public void onPurchaseRestoreFinished(List<THIABPurchase> consumed,
                                List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed)
                        {
                            Timber.d("onPurchaseRestoreFinished3");
                            if (progressDialog != null)
                            {
                                progressDialog.hide();
                            }

                            Context currentContext = currentActivityHolder.getCurrentContext();
                            if (currentContext != null)
                            {
                                IABPurchaseRestorerAlertUtil.handlePurchaseRestoreFinished(
                                        currentContext,
                                        consumed,
                                        reportFailed,
                                        consumeFailed,
                                        IABPurchaseRestorerAlertUtil.createFailedRestoreClickListener(
                                                currentContext, new Exception()),
                                        true); // TODO have a better exception
                            }
                        }

                        @Override
                        public void onPurchaseRestoreFinished(List<THIABPurchase> consumed,
                                List<THIABPurchase> consumeFailed)
                        {
                            Timber.d("onPurchaseRestoreFinished2");
                        }

                        @Override public void onPurchaseRestoreFailed(IABException iabException)
                        {
                            Timber.e(iabException, "onPurchaseRestoreFailed");
                            Context currentContext = currentActivityHolder.getCurrentContext();
                            if (currentContext != null)
                            {
                                IABPurchaseRestorerAlertUtil.popSendEmailSupportRestoreFailed(
                                        currentContext, iabException);
                            }
                        }
                    };
        }

        if (followCallback == null)
        {
            createFollowCallback();
        }
    }

    @Override
    protected BillingPurchaser.OnPurchaseFinishedListener<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase, IABException> createPurchaseFinishedListener()
    {
        return new THIABUserInteractorOnPurchaseFinishedListener();
    }

    @Override
    protected PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> createPurchaseReportedListener()
    {
        return new THIABUserInteractorOnPurchaseReportedListener();
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

    @Override
    protected BillingAlertDialogUtil<IABSKU, THIABProductDetail, THIABLogicHolder, StoreSKUDetailView, THSKUDetailsAdapter> getBillingAlertDialogUtil()
    {
        return THIABAlertDialogUtil;
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

    @Override
    protected OnBillingAvailableListener<IABException> createPurchaseVirtualDollarWhenAvailableListener(
            OwnedPortfolioId ownedPortfolioId)
    {
        return new THIABUserInteractorPurchaseVirtualDollarWhenAvailableListener(ownedPortfolioId);
    }

    protected class THIABUserInteractorPurchaseVirtualDollarWhenAvailableListener
            extends THBaseBillingInteractorPurchaseVirtualDollarWhenAvailableListener
    {
        public THIABUserInteractorPurchaseVirtualDollarWhenAvailableListener(
                OwnedPortfolioId portfolioId)
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
        followCallback =
                new UserInteractorFollowHeroCallback(heroListCache.get(), userProfileCache.get());
    }

    protected boolean hadErrorLoadingInventory()
    {
        THIABLogicHolder billingActorCopy = this.billingActor;
        return billingActorCopy != null && billingActorCopy.getInventoryFetcherHolder()
                .hadErrorLoadingInventory();
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
        AlertDialog alertDialog = null;
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentContext);
            alertDialogBuilder
                    .setTitle(R.string.store_billing_error_loading_window_title)
                    .setMessage(R.string.store_billing_error_loading_window_description)
                    .setCancelable(true)
                    .setPositiveButton(R.string.store_billing_error_loading_act,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    if (inventoryFetchedForgetListener == null)
                                    {
                                        inventoryFetchedForgetListener =
                                                createForgetFetchedListener();
                                    }
                                    int requestCode =
                                            getBillingLogicHolder().getUnusedRequestCode();
                                    getBillingLogicHolder().getInventoryFetcherHolder()
                                            .registerInventoryFetchedListener(requestCode,
                                                    inventoryFetchedForgetListener);
                                    getBillingLogicHolder().getInventoryFetcherHolder()
                                            .launchInventoryFetchSequence(requestCode,
                                                    new ArrayList<IABSKU>());
                                }
                            });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return alertDialog;
    }

    protected BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> createForgetFetchedListener()
    {
        return new BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>()
        {
            @Override
            public void onInventoryFetchSuccess(int requestCode, List<IABSKU> productIdentifiers,
                    Map<IABSKU, THIABProductDetail> inventory)
            {
                getBillingLogicHolder().forgetRequestCode(requestCode);
            }

            @Override
            public void onInventoryFetchFail(int requestCode, List<IABSKU> productIdentifiers,
                    IABException exception)
            {
                getBillingLogicHolder().forgetRequestCode(requestCode);
            }
        };
    }

    //<editor-fold desc="Pop SKU list">
    public void conditionalPopBuyVirtualDollars()
    {
        String language = Locale.getDefault().getLanguage();
        Timber.d("lyl language=%s", language);
        if ("zh".equals(language) || true)
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS);
        }
        else
        {
            conditionalPopBuyVirtualDollars(null);
        }
    }

    public void conditionalPopBuyVirtualDollars(Runnable runOnPurchaseComplete)
    {
        if (popErrorConditional() == null)
        {
            popBuyVirtualDollars(runOnPurchaseComplete);
        }
    }

    public void alipayPopBuy(int type)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(currentActivityHolder.getCurrentActivity());
        int array = 0;
        switch (type)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                array = R.array.alipay_virtual_dollars_array;
                break;
            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                array = R.array.alipay_follow_credits_array;
                break;
            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                array = R.array.alipay_stock_alerts_array;
                break;
            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                array = R.array.alipay_reset_portfolio_array;
                break;
        }
        final int type1 = type;
        builder.setTitle(R.string.app_name)
                .setItems(currentActivityHolder.getCurrentActivity()
                        .getResources()
                        .getStringArray(array),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (checkAlertsPlan(which, type1))
                                {
                                    Intent intent =
                                            new Intent(currentActivityHolder.getCurrentActivity(),
                                                    AlipayActivity.class);
                                    intent.putExtra(AlipayActivity.ALIPAY_TYPE_KEY, type1);
                                    intent.putExtra(AlipayActivity.ALIPAY_POSITION_KEY, which);
                                    currentActivityHolder.getCurrentActivity()
                                            .startActivity(intent);
                                }
                            }
                        });
        builder.create().show();
    }

    //TODO refactor
    private boolean checkAlertsPlan(int which, int type1)
    {
        if (type1 != StoreItemAdapter.POSITION_BUY_STOCK_ALERTS)
        {
            return true;
        }
        AlertSlotDTO alertSlots =
                securityAlertCountingHelperLazy.get().getAlertSlots(
                        currentUserId.toUserBaseKey());
        switch (which)
        {
            case 0:
                if (alertSlots.totalAlertSlots >= 2)
                {
                    alertDialogUtilLazy.get()
                            .showDefaultDialog(
                                    currentActivityHolder.getCurrentContext(),
                                    R.string.store_billing_error_buy_alerts);
                    return false;
                }
                break;
            case 1:
                if (alertSlots.totalAlertSlots >= 5)
                {
                    alertDialogUtilLazy.get()
                            .showDefaultDialog(
                                    currentActivityHolder.getCurrentContext(),
                                    R.string.store_billing_error_buy_alerts);
                    return false;
                }
                break;
            case 2:
                break;
        }
        return true;
    }

    public void popBuyVirtualDollars(Runnable runOnPurchaseComplete)
    {
        localyticsSession.tagEvent(LocalyticsConstants.BuyExtraCashDialog_Show);
        popBuyDialog(THBillingInteractor.DOMAIN_VIRTUAL_DOLLAR,
                R.string.store_buy_virtual_dollar_window_title, runOnPurchaseComplete);
    }

    public void conditionalPopBuyFollowCredits()
    {
        String language = Locale.getDefault().getLanguage();
        Timber.d("lyl language=%s", language);
        if ("zh".equals(language) || true)
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS);
        }
        else
        {
            conditionalPopBuyFollowCredits(null);
        }
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
        localyticsSession.tagEvent(LocalyticsConstants.BuyCreditsDialog_Show);
        popBuyDialog(THBillingInteractor.DOMAIN_FOLLOW_CREDITS,
                R.string.store_buy_follow_credits_window_message, runOnPurchaseComplete);
    }

    public void conditionalPopBuyStockAlerts()
    {
        String language = Locale.getDefault().getLanguage();
        Timber.d("lyl language=%s", language);
        if ("zh".equals(language) || true)
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_STOCK_ALERTS);
        }
        else
        {
            conditionalPopBuyStockAlerts(null);
        }
    }

    public void conditionalPopBuyStockAlerts(Runnable runOnPurchaseComplete)
    {
        //TODO need jump to alipay
        String language = Locale.getDefault().getLanguage();
        Timber.d("lyl language=%s", language);
        if ("zh".equals(language) || true)
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_STOCK_ALERTS);
        }
        else if (popErrorConditional() == null)
        {
            popBuyStockAlerts(runOnPurchaseComplete);
        }
    }

    public void popBuyStockAlerts(Runnable runOnPurchaseComplete)
    {
        popBuyDialog(THBillingInteractor.DOMAIN_STOCK_ALERTS,
                R.string.store_buy_stock_alerts_window_title, runOnPurchaseComplete);
    }

    public void conditionalPopBuyResetPortfolio()
    {
        String language = Locale.getDefault().getLanguage();
        Timber.d("lyl language=%s", language);
        if ("zh".equals(language) || true)
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO);
        }
        else
        {
            conditionalPopBuyResetPortfolio(null);
        }
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
        localyticsSession.tagEvent(LocalyticsConstants.ResetPortfolioDialog_Show);
        popBuyDialog(THBillingInteractor.DOMAIN_RESET_PORTFOLIO,
                R.string.store_buy_reset_portfolio_window_title, runOnPurchaseComplete);
    }

    //</editor-fold>

    //<editor-fold desc="Purchasing Sequence">
    @Override protected void launchPurchaseSequence(IABSKU productIdentifier)
    {
        launchPurchaseSequence(new THIABPurchaseOrder(productIdentifier, applicablePortfolioId));
    }

    @Override protected void launchPurchaseSequence(THIABPurchaseOrder purchaseOrder)
    {
        THIABLogicHolder logicHolder = getBillingLogicHolder();
        if (logicHolder != null)
        {
            launchPurchaseSequence(logicHolder.getPurchaserHolder(), purchaseOrder);
        }
        else
        {
            Timber.e(new NullPointerException("logicHolder just became null for " + purchaseOrder),
                    "logicHolder just became null for " + purchaseOrder);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Reporting Sequence">
    @Override protected void launchReportPurchaseSequence(THIABPurchase purchase)
    {
        launchReportPurchaseSequence(getBillingLogicHolder().getPurchaseReporterHolder(), purchase);
    }

    protected void handlePurchaseReportSuccess(THIABPurchase reportedPurchase,
            UserProfileDTO updatedUserProfile)
    {
        super.handlePurchaseReportSuccess(reportedPurchase, updatedUserProfile);
        launchConsumeSequence(reportedPurchase);
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Consumption Sequence">
    protected void launchConsumeSequence(THIABPurchase reportedPurchase)
    {
        launchConsumeSequence(getBillingLogicHolder().getPurchaseConsumerHolder(),
                reportedPurchase);
    }

    protected void launchConsumeSequence(THIABPurchaseConsumerHolder consumerHolder,
            THIABPurchase reportedPurchase)
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
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                dialog.setMessage(currentContext.getString(
                        R.string.store_billing_report_api_finishing_window_title));
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
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = ProgressDialog.show(
                    currentContext,
                    currentContext.getString(
                            R.string.store_billing_restoring_purchase_window_title),
                    currentContext.getString(
                            R.string.store_billing_restoring_purchase_window_message),
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
            userProfileDTO = userProfileCache.get().get(currentUserIdLazy.get().toUserBaseKey());
        }
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
            String language = Locale.getDefault().getLanguage();
            Timber.d("lyl language=%s", language);
            if ("zh".equals(language) || true)
            {
                alipayPopBuy(StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS);
                return;
            }
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
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                progressDialog = ProgressDialog.show(
                        currentContext,
                        currentContext.getString(R.string.manage_heroes_follow_progress_title),
                        currentContext.getResources()
                                .getString(R.string.manage_heroes_follow_progress_message),
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
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = ProgressDialog.show(
                    currentContext,
                    currentContext.getString(R.string.manage_heroes_unfollow_progress_title),
                    currentContext.getString(R.string.manage_heroes_unfollow_progress_message),
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
        public UserInteractorFollowHeroCallback(HeroListCache heroListCache,
                UserProfileCache userProfileCache)
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

    protected class THIABUserInteractorOnPurchaseFinishedListener
            extends THBaseBillingInteractorOnPurchaseFinishedListener
    {
        public THIABUserInteractorOnPurchaseFinishedListener()
        {
            super();
        }

        @Override public void onPurchaseFinished(int requestCode, THIABPurchaseOrder purchaseOrder,
                THIABPurchase purchase)
        {
            super.onPurchaseFinished(requestCode, purchaseOrder, purchase);
            launchReportPurchaseSequence(purchase);
        }

        @Override public void onPurchaseFailed(int requestCode, THIABPurchaseOrder purchaseOrder,
                IABException exception)
        {
            super.onPurchaseFailed(requestCode, purchaseOrder, exception);
            Context currentContext = currentActivityHolder.getCurrentContext();
            if (currentContext != null)
            {
                if (exception instanceof IABVerificationFailedException)
                {
                    THIABAlertDialogUtil.popVerificationFailed(currentContext);
                }
                else if (exception instanceof IABUserCancelledException)
                {
                    THIABAlertDialogUtil.popUserCancelled(currentContext);
                }
                else if (exception instanceof IABBadResponseException)
                {
                    THIABAlertDialogUtil.popBadResponse(currentContext);
                }
                else if (exception instanceof IABRemoteException)
                {
                    THIABAlertDialogUtil.popRemoteError(currentContext);
                }
                else if (exception instanceof IABItemAlreadyOwnedException)
                {
                    THIABAlertDialogUtil.popSKUAlreadyOwned(currentContext,
                            thiabProductDetailCache.get()
                                    .get(purchaseOrder.getProductIdentifier()));
                }
                else if (exception instanceof IABSendIntentException)
                {
                    THIABAlertDialogUtil.popSendIntent(currentContext);
                }
                else
                {
                    THIABAlertDialogUtil.popUnknownError(currentContext);
                }
            }
        }
    }

    protected class THIABUserInteractorOnPurchaseReportedListener
            extends THBaseBillingInteractorOnPurchaseReportedListener
    {
        public THIABUserInteractorOnPurchaseReportedListener()
        {
            super();
        }
    }
}
