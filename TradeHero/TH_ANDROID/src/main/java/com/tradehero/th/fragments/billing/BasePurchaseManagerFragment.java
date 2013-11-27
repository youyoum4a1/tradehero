package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
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
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.IABAlertSKUUtils;
import com.tradehero.th.billing.googleplay.IABAlertUtils;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.googleplay.THIABProductDetails;
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
        implements IABAlertUtils.OnDialogSKUDetailsClickListener<THIABProductDetails>,
        THIABActorUser,
        PurchaseReporter.OnPurchaseReportedListener<
                IABSKU,
                THIABOrderId,
                BaseIABPurchase,
                Exception>,
        BillingPurchaser.OnPurchaseFinishedListener<
                IABSKU,
                THIABPurchaseOrder,
                THIABOrderId,
                BaseIABPurchase,
                IABException>
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
    protected int requestCode = (int) (Math.random() * Integer.MAX_VALUE);
    protected THIABPurchaseOrder purchaseOrder;
    protected UserBaseKey userBaseKey;
    protected BaseIABPurchase purchase;

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setBillingActor(((THIABActorUser) getActivity()).getBillingActor());
    }

    @Override public void onResume()
    {
        super.onResume();
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
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
            progressDialog = null;
        }
        super.onDestroyView();
    }

    protected void preparePrerequisites()
    {
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
        // At this stage, we know the applicable portfolio is available
        runWhatWaitingForSkuDetailsMilestone();
    }

    protected void runWhatWaitingForSkuDetailsMilestone()
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
        Runnable runnable = runOnShowSkuDetailsMilestoneComplete;
        if (runnable != null)
        {
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
            popDialogLoadingInfo();
            runOnShowSkuDetailsMilestoneComplete = runnable;
            if (showSkuDetailsMilestone.isFailed() || !showSkuDetailsMilestone.isRunning())
            {
                showSkuDetailsMilestone.launch();
            }
        }
    }

    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return portfolioCompactListCache.get().getDefaultPortfolio(userBaseKey);
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
            IABAlertUtils.popBillingUnavailable(getActivity());
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
        if (!popErrorConditional())
        {
            popBuyVirtualDollars();
        }
    }

    protected void popBuyVirtualDollars()
    {
        popBuyDialog(THIABProductDetails.DOMAIN_VIRTUAL_DOLLAR, R.string.store_buy_virtual_dollar_window_title);
    }

    protected void conditionalPopBuyFollowCredits()
    {
        if (!popErrorConditional())
        {
            popBuyFollowCredits();
        }
    }

    protected void popBuyFollowCredits()
    {
        popBuyDialog(THIABProductDetails.DOMAIN_FOLLOW_CREDITS, R.string.store_buy_follow_credits_window_message);
    }

    protected void conditionalPopBuyStockAlerts()
    {
        if (!popErrorConditional())
        {
            popBuyStockAlerts();
        }
    }

    protected void popBuyStockAlerts()
    {
        popBuyDialog(THIABProductDetails.DOMAIN_STOCK_ALERTS, R.string.store_buy_stock_alerts_window_title);
    }

    protected void conditionalPopBuyResetPortfolio()
    {
        if (!popErrorConditional())
        {
            popBuyResetPortfolio();
        }
    }

    protected void popBuyResetPortfolio()
    {
        popBuyDialog(THIABProductDetails.DOMAIN_RESET_PORTFOLIO, R.string.store_buy_reset_portfolio_window_title);
    }

    protected void popBuyDialog(final String skuDomain, final int titleResId)
    {
        waitForSkuDetailsMilestoneComplete(new Runnable()
        {
            @Override public void run()
            {
                getView().post(new Runnable()
                {
                    @Override public void run()
                    {
                        IABAlertSKUUtils.popBuyDialog(getActivity(), getBillingActor(), BasePurchaseManagerFragment.this, skuDomain, titleResId);
                    }
                });
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="IABAlertUtils.OnDialogSKUDetailsClickListener">
    @Override public void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, THIABProductDetails skuDetails)
    {
        THIABActor actor = getBillingActor();
        if (actor != null)
        {
            launchPurchaseSequence(actor, new THIABPurchaseOrder(skuDetails.getProductIdentifier(), getApplicablePortfolioId()));
        }
        else
        {
            THLog.d(TAG, "IABActor was null");
        }
    }
    //</editor-fold>

    protected void launchPurchaseSequence(THIABActor actor, THIABPurchaseOrder purchaseOrder)
    {
        this.purchaseOrder = purchaseOrder;
        this.requestCode = actor.registerPurchaseFinishedListener(this);
        actor.launchPurchaseSequence(requestCode, purchaseOrder);
    }

    //<editor-fold desc="BillingPurchaser.OnPurchaseFinishedListener">
    @Override public void onPurchaseFailed(int requestCode, THIABPurchaseOrder purchaseOrder, IABException exception)
    {
        THLog.e(TAG, "onPurchaseFailed requestCode " + requestCode ,exception);
        if (this.requestCode != requestCode)
        {
            THLog.d(TAG, "handlePurchaseException. Received requestCode " + requestCode + ", when in fact it expects " + this.requestCode);
        }
        else if (exception instanceof IABVerificationFailedException)
        {
            IABAlertUtils.popVerificationFailed(getActivity());
        }
        else if (exception instanceof IABUserCancelledException)
        {
            IABAlertUtils.popUserCancelled(getActivity());
        }
        if (exception instanceof IABBadResponseException)
        {
            IABAlertUtils.popBadResponse(getActivity());
        }
        else if (exception instanceof IABRemoteException)
        {
            IABAlertUtils.popRemoteError(getActivity());
        }
        else if (exception instanceof IABAlreadyOwnedException)
        {
            IABAlertUtils.popSKUAlreadyOwned(getActivity(), skuDetailCache.get().get(purchaseOrder.getProductIdentifier()));
        }
        else if (exception instanceof IABSendIntentException)
        {
            IABAlertUtils.popSendIntent(getActivity());
        }
        else
        {
            IABAlertUtils.popUnknownError(getActivity());
        }
    }

    @Override public void onPurchaseFinished(int requestCode, THIABPurchaseOrder purchaseOrder, BaseIABPurchase purchase)
    {
        if (this.requestCode != requestCode)
        {
            THLog.d(TAG, "handlePurchaseReceived. Received requestCode " + requestCode + ", when in fact it expects " + this.requestCode);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReceived. Received requestCode " + requestCode + ", purchase " + purchase);
            handlePurchaseSuccess(requestCode, purchase);
        }
    }
    //</editor-fold>

    protected void handlePurchaseSuccess(int requestCode, BaseIABPurchase purchase)
    {
        this.purchase = purchase;
        reportPurchaseToServerAPI(purchase);
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
        IABAlertUtils.popFailedToLoadRequiredInfo(getActivity());
    }

    protected void reportPurchaseToServerAPI(BaseIABPurchase purchase)
    {
        progressDialog = ProgressDialog.show(
                getActivity(),
                Application.getResourceString(R.string.store_billing_report_api_launching_window_title),
                Application.getResourceString(R.string.store_billing_report_api_launching_window_message),
                true);
        int requestCode = getBillingActor().registerPurchaseReportedHandler(this);
        getBillingActor().launchReportSequence(requestCode, purchase);
    }

    //<editor-fold desc="BasePurchaseReporter.OnPurchaseReportedListener">
    @Override public void onPurchaseReported(int requestCode, BaseIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        if (!reportedPurchase.getProductIdentifier().equals(purchase.getProductIdentifier()))
        {
            THLog.d(TAG, "This was not the purchase we reported. Expected " + purchase.getProductIdentifier() + ", actual " + reportedPurchase.getProductIdentifier());
        }
        else if (!reportedPurchase.getOrderId().equals(purchase.getOrderId()))
        {
            THLog.d(TAG, "This was not the purchase we reported. Expected " + purchase.getOrderId() + ", actual " + reportedPurchase.getOrderId());
        }
        else
        {
            handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
        }
    }

    protected void handlePurchaseReportSuccess(BaseIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        userProfileCache.get().put(updatedUserPortfolio.getBaseKey(), updatedUserPortfolio);

        ProgressDialog dialog = progressDialog;
        if (dialog != null)
        {
            dialog.setTitle("Done");
            dialog.setMessage("Closing");
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
            }
        }, 500);

        // TODO consume
    }



    @Override public void onPurchaseReportFailed(int requestCode, BaseIABPurchase reportedPurchase, Exception error)
    {
        THLog.e(TAG, "Failed to report to server", error);
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
        IABAlertUtils.popFailedToReport(getActivity());
    }
    //</editor-fold>
}
