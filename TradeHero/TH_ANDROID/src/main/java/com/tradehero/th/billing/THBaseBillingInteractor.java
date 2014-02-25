package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
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
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
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
        BillingLogicHolderType extends BillingLogicHolder<
                ProductIdentifierType,
                ProductDetailType,
                PurchaseOrderType,
                OrderIdType,
                ProductPurchaseType,
                BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements BillingInteractor<
        ProductIdentifierType,
        ProductDetailType,
        PurchaseOrderType,
        OrderIdType,
        ProductPurchaseType,
        BillingLogicHolderType,
        BillingExceptionType>
{
    @Inject protected BillingAlertDialogUtil billingAlertDialogUtil;
    @Inject protected CurrentActivityHolder currentActivityHolder;
    @Inject protected CurrentUserId currentUserId;
    protected UserProfileDTO userProfileDTO;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    protected OwnedPortfolioId applicablePortfolioId;
    @Inject protected Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    protected ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    public THBaseBillingInteractor()
    {
        super();
    }
    //</editor-fold>

    public void onPause()
    {
    }

    public void onStop()
    {
    }

    public void onDestroy()
    {
        runOnShowProductDetailsMilestoneComplete = null;
        if (progressDialog != null)
        {
            progressDialog.hide();
            progressDialog = null;
        }
        showProductDetailsMilestoneListener = null;
    }

    //<editor-fold desc="Billing Available">
    @Override public Boolean isBillingAvailable()
    {
        BillingLogicHolderType billingActorCopy = this.getBillingLogicHolder();
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
        return billingAlertDialogUtil.popBillingUnavailable(
                currentActivityHolder.getCurrentActivity(),
                getBillingLogicHolder().getBillingHolderName(
                        currentActivityHolder.getCurrentActivity().getResources()));
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
        prepareOwnedPortfolioId();
        prepareProductDetailsPrerequisites();
    }

    protected void prepareOwnedPortfolioId()
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
        if (showProductDetailsMilestone.isComplete())
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
            if (showProductDetailsMilestone.isFailed() || !showProductDetailsMilestone.isRunning())
            {
                showProductDetailsMilestone.launch();
            }
            else
            {
                Timber.d("showProductDetailsMilestone is already running");
            }
        }
    }

    protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    protected void handleShowProductDetailsMilestoneComplete()
    {
        // At this stage, we know the applicable portfolio is available in the cache
        if (this.applicablePortfolioId.portfolioId == null)
        {
            this.applicablePortfolioId = portfolioCompactListCache.get().getDefaultPortfolio(this.applicablePortfolioId.getUserBaseKey());
        }
        // We also know that the userProfile is in the cache
        this.userProfileDTO = userProfileCache.get().get(this.applicablePortfolioId.getUserBaseKey());

        runWhatWaitingForProductDetailsMilestone();
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
                            runOnShowProductDetailsMilestoneComplete = null;
                        }
                    });
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
        }
    }

}
