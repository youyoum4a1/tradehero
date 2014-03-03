package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.BillingPurchaserHolder;
import com.tradehero.common.billing.OnBillingAvailableListener;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class THBaseBillingInteractor<
        ProductIdentifierListKey extends DTOKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        PurchaseOrderType extends PurchaseOrder<ProductIdentifierType>,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<
                ProductIdentifierType,
                OrderIdType>,
        THBillingLogicHolderType extends THBillingLogicHolder<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                ProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                ProductDetailType,
                ProductDetailViewType>,
        BillingPurchaseFinishedListenerType extends BillingPurchaser.OnPurchaseFinishedListener<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingPurchaserHolderType extends BillingPurchaserHolder<
                ProductIdentifierType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingPurchaseFinishedListenerType,
                BillingExceptionType>,
        PurchaseReportedListenerType extends PurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        PurchaseReporterHolderType extends PurchaseReporterHolder<
                ProductIdentifierType,
                OrderIdType,
                ProductPurchaseType,
                PurchaseReportedListenerType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
        implements THBillingInteractor<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                THBillingLogicHolderType,
                BillingExceptionType>,
            BillingAlertDialogUtil.OnDialogProductDetailClickListener<ProductDetailType>

{
    @Inject protected CurrentActivityHolder currentActivityHolder;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    protected UserProfileDTO userProfileDTO;
    protected OwnedPortfolioId applicablePortfolioId;

    protected ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    public THBaseBillingInteractor()
    {
        super();
        DaggerUtils.inject(this);
        prepareCallbacks();
    }
    //</editor-fold>

    //<editor-fold desc="Life Cycle">
    protected void prepareCallbacks()
    {
        purchaseFinishedListener = createPurchaseFinishedListener();
        purchaseReportedListener = createPurchaseReportedListener();
    }

    public void onPause()
    {
    }

    public void onStop()
    {
    }

    public void onDestroy()
    {
        purchaseFinishedListener = null;
        runOnShowProductDetailsMilestoneComplete = null;
        if (progressDialog != null)
        {
            progressDialog.hide();
            progressDialog = null;
        }
        showProductDetailsMilestoneListener = null;
    }
    //</editor-fold>

    //<editor-fold desc="Logic Holder handling">
    protected void haveLogicHolderForget(int requestCode)
    {
        THBillingLogicHolderType actor = this.getBillingLogicHolder();
        if (actor != null)
        {
            actor.forgetRequestCode(requestCode);
        }
    }
    //</editor-fold>

    abstract protected BillingAlertDialogUtil<
        ProductIdentifierType,
        ProductDetailType,
        THBillingLogicHolderType,
        ProductDetailViewType,
        ProductDetailAdapterType> getBillingAlertDialogUtil();

    //<editor-fold desc="Billing Available">
    @Override public Boolean isBillingAvailable()
    {
        THBillingLogicHolderType billingActorCopy = this.getBillingLogicHolder();
        return billingActorCopy == null ? null : billingActorCopy.isBillingAvailable();
    }

    @Override public AlertDialog conditionalPopBillingNotAvailable()
    {
        Boolean billingAvailable = isBillingAvailable();
        if (billingAvailable == null || !billingAvailable) // TODO wait when is null
        {
            return popBillingUnavailable();
        }
        return null;
    }

    protected void postPopBillingUnavailable()
    {
        currentActivityHolder.getCurrentHandler().post(new Runnable()
        {
            @Override public void run()
            {
                popBillingUnavailable();
            }
        });
    }

    @Override public AlertDialog popBillingUnavailable()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            return getBillingAlertDialogUtil().popBillingUnavailable(
                    currentActivityHolder.getCurrentContext(),
                    getBillingLogicHolder().getBillingHolderName(
                            currentContext.getResources()));
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Portfolio Application">
    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return applicablePortfolioId;
    }

    public void setApplicablePortfolioId(OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
        prepareProductDetailsPrerequisites();
    }
    //</editor-fold>

    //<editor-fold desc="Inventory Preparation">
    protected ShowProductDetailsMilestone showProductDetailsMilestone;
    protected Milestone.OnCompleteListener showProductDetailsMilestoneListener;
    protected Runnable runOnShowProductDetailsMilestoneComplete;

    abstract protected void prepareProductDetailsPrerequisites();

    protected void prepareProductDetailsPrerequisites(ProductIdentifierListKey listKey)
    {
        showProductDetailsMilestone = createShowProductDetailsMilestone(listKey);
        showProductDetailsMilestone.setOnCompleteListener(showProductDetailsMilestoneListener);
        showProductDetailsMilestone.launch();
    }

    abstract protected ShowProductDetailsMilestone createShowProductDetailsMilestone(ProductIdentifierListKey listKey);

    public void waitForSkuDetailsMilestoneComplete(Runnable runnable)
    {
        if (showProductDetailsMilestone != null && showProductDetailsMilestone.isComplete())
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
                runOnShowProductDetailsMilestoneComplete = runnable;
            }
            if (showProductDetailsMilestone != null && (showProductDetailsMilestone.isFailed() || !showProductDetailsMilestone.isRunning()))
            {
                showProductDetailsMilestone.launch();
            }
            else
            {
                Timber.d("showProductDetailsMilestone is null or already running");
            }
        }
    }

    protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
        // TODO add a wait to inform the user
    }

    protected void handleShowProductDetailsMilestoneComplete()
    {
        // At this stage, we know the applicable portfolio is available in the cache
        if (this.applicablePortfolioId != null)
        {
            if (this.applicablePortfolioId.portfolioId == null)
            {
                this.applicablePortfolioId = portfolioCompactListCache.get().getDefaultPortfolio(this.applicablePortfolioId.getUserBaseKey());
            }

            // We also know that the userProfile is in the cache
            this.userProfileDTO = userProfileCache.get().get(this.applicablePortfolioId.getUserBaseKey());

            runWhatWaitingForProductDetailsMilestone();
        }
    }

    protected void runWhatWaitingForProductDetailsMilestone()
    {
        Runnable runnable = runOnShowProductDetailsMilestoneComplete;
        if (runnable != null)
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            runOnShowProductDetailsMilestoneComplete = null;
            runnable.run();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Product Detail Presentation">
    public void popBuyDialog(final String skuDomain, final int titleResId, final Runnable runOnPurchaseComplete)
    {
        waitForSkuDetailsMilestoneComplete(new Runnable()
        {
            @Override public void run()
            {
                Handler handler = currentActivityHolder.getCurrentHandler();
                Timber.d("handler %s", handler);
                if (handler != null)
                {
                    handler.post(createShowProductDetailRunnable(skuDomain, titleResId,
                            runOnPurchaseComplete));
                }
            }
        });
    }

    protected Runnable createShowProductDetailRunnable(final String skuDomain, final int titleResId, final Runnable runOnPurchaseComplete)
    {
        return new THBaseBillingInteractShowProductDetailRunnable(skuDomain, titleResId, runOnPurchaseComplete);
    }

    protected class THBaseBillingInteractShowProductDetailRunnable implements Runnable
    {
        final public String skuDomain;
        final public int titleResId;
        protected Runnable runOnPurchaseComplete;

        public THBaseBillingInteractShowProductDetailRunnable(final String skuDomain, final int titleResId, final Runnable runOnPurchaseComplete)
        {
            super();
            this.skuDomain = skuDomain;
            this.titleResId = titleResId;
            this.runOnPurchaseComplete = runOnPurchaseComplete;
        }

        public void setRunOnPurchaseComplete(Runnable runOnPurchaseComplete)
        {
            this.runOnPurchaseComplete = runOnPurchaseComplete;
        }

        @Override public void run()
        {
            Activity currentActivity = currentActivityHolder.getCurrentActivity();
            if (currentActivity != null)
            {
                getBillingAlertDialogUtil().popBuyDialog(
                        currentActivity,
                        getTHBillingLogicHolder(),
                        THBaseBillingInteractor.this,
                        skuDomain,
                        titleResId,
                        runOnPurchaseComplete);
            }
        }
    }

    protected Runnable runOnPurchaseComplete;

    @Override public void onDialogProductDetailClicked(DialogInterface dialogInterface,
            int position, ProductDetailType productDetail, Runnable runOnPurchaseComplete)
    {
        this.runOnPurchaseComplete = runOnPurchaseComplete;
        launchPurchaseSequence(productDetail.getProductIdentifier());
    }
    //</editor-fold>

    //<editor-fold desc="Purchasing Sequence">
    protected BillingPurchaseFinishedListenerType purchaseFinishedListener;
    abstract protected BillingPurchaseFinishedListenerType createPurchaseFinishedListener();
    abstract protected void launchPurchaseSequence(ProductIdentifierType productIdentifier);
    abstract protected void launchPurchaseSequence(PurchaseOrderType purchaseOrder);

    protected class THBaseBillingInteractorOnPurchaseFinishedListener implements BillingPurchaser.OnPurchaseFinishedListener<
            ProductIdentifierType,
            PurchaseOrderType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>
    {
        public THBaseBillingInteractorOnPurchaseFinishedListener()
        {
            super();
        }

        @Override public void onPurchaseFinished(int requestCode, PurchaseOrderType purchaseOrder, ProductPurchaseType purchase)
        {
            haveLogicHolderForget(requestCode);
            // Children should call report or whatever is relevant
        }

        @Override public void onPurchaseFailed(int requestCode, PurchaseOrderType purchaseOrder, BillingExceptionType billingException)
        {
            haveLogicHolderForget(requestCode);
            runOnPurchaseComplete = null;
            Timber.e("onPurchaseFailed requestCode %d", requestCode, billingException);
        }
    }

    protected void launchPurchaseSequence(BillingPurchaserHolderType purchaserHolder, PurchaseOrderType purchaseOrder)
    {
        int requestCode = getBillingLogicHolder().getUnusedRequestCode();
        purchaserHolder.registerPurchaseFinishedListener(requestCode, purchaseFinishedListener);
        purchaserHolder.launchPurchaseSequence(requestCode, purchaseOrder);
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Reporting Sequence">
    protected PurchaseReportedListenerType purchaseReportedListener;
    abstract protected PurchaseReportedListenerType createPurchaseReportedListener();
    abstract protected void launchReportPurchaseSequence(ProductPurchaseType purchase);

    protected class THBaseBillingInteractorOnPurchaseReportedListener implements PurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType,
            BillingExceptionType>
    {
        public THBaseBillingInteractorOnPurchaseReportedListener()
        {
            super();
        }

        @Override public void onPurchaseReported(int requestCode, ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
        {
            haveLogicHolderForget(requestCode);
            handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
            // Children should continue with the sequence
        }

        @Override public void onPurchaseReportFailed(int requestCode, ProductPurchaseType reportedPurchase, BillingExceptionType error)
        {
            haveLogicHolderForget(requestCode);
            runOnPurchaseComplete = null;
            Timber.e("Failed to report to server", error);
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
            getBillingAlertDialogUtil().popFailedToReport(currentActivityHolder.getCurrentActivity());
        }
    }

    protected void launchReportPurchaseSequence(PurchaseReporterHolderType purchaseReporterHolder, ProductPurchaseType purchase)
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = ProgressDialog.show(
                    currentContext,
                    currentContext.getString(R.string.store_billing_report_api_launching_window_title),
                    currentContext.getString(R.string.store_billing_report_api_launching_window_message),
                    true);
        }
        int requestCode = getBillingLogicHolder().getUnusedRequestCode();
        purchaseReporterHolder.registerPurchaseReportedListener(requestCode, purchaseReportedListener);
        purchaseReporterHolder.launchReportSequence(requestCode, purchase);
    }

    protected void handlePurchaseReportSuccess(ProductPurchaseType reportedPurchase, UserProfileDTO updatedUserProfile)
    {
        userProfileDTO = updatedUserProfile;
        userProfileCache.get().put(updatedUserProfile.getBaseKey(), updatedUserProfile);
    }
    //</editor-fold>

    //<editor-fold desc="Purchase Virtual Dollars">
    protected OnPurchaseVirtualDollarListener purchaseVirtualDollarListener;

    abstract public void purchaseVirtualDollar(OwnedPortfolioId ownedPortfolioId);

    public void setPurchaseVirtualDollarListener(OnPurchaseVirtualDollarListener purchaseVirtualDollarListener)
    {
        this.purchaseVirtualDollarListener = purchaseVirtualDollarListener;
    }

    abstract protected OnBillingAvailableListener<BillingExceptionType> createPurchaseVirtualDollarWhenAvailableListener(OwnedPortfolioId ownedPortfolioId);

    abstract protected class THBaseBillingInteractorPurchaseVirtualDollarWhenAvailableListener implements OnBillingAvailableListener<BillingExceptionType>
    {
        protected OwnedPortfolioId applicablePortfolioId;

        public THBaseBillingInteractorPurchaseVirtualDollarWhenAvailableListener(OwnedPortfolioId portfolioId)
        {
            super();
            this.applicablePortfolioId = portfolioId;
        }

        @Override public void onBillingNotAvailable(BillingExceptionType billingException)
        {
            OnPurchaseVirtualDollarListener listenerCopy = purchaseVirtualDollarListener;
            if (listenerCopy != null)
            {
                listenerCopy.onPurchasedVirtualDollarFailed(applicablePortfolioId, billingException);
            }
        }
    }
    //</editor-fold>

    protected void popDialogLoadingInfo()
    {
        Context currentContext = currentActivityHolder.getCurrentContext();
        if (currentContext != null)
        {
            progressDialog = ProgressDialogUtil.show(
                    currentContext,
                    R.string.store_billing_loading_info_window_title,
                    R.string.store_billing_loading_info_window_message
            );
            progressDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener()
                    {
                        @Override public void onCancel(DialogInterface dialog)
                        {
                            runOnShowProductDetailsMilestoneComplete = null;
                        }
                    });
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
    }
}
